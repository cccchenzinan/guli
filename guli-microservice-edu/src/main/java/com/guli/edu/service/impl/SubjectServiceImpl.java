package com.guli.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guli.common.utils.ExcelImportUtil;
import com.guli.edu.vo.SubjectNestedVo;
import com.guli.edu.entity.Subject;
import com.guli.edu.mapper.SubjectMapper;
import com.guli.edu.service.SubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guli.edu.vo.SubjectVo;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author czn
 * @since 2019-07-12
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Override
    public List<String> batchImport(MultipartFile file) throws Exception {

        //创建错误提示列表
        List<String> errorMsg = new ArrayList<>();

        //读取文件
        ExcelImportUtil excelHSSFUtil = new ExcelImportUtil(file.getInputStream());

        HSSFSheet sheet = excelHSSFUtil.getSheet();
        int rowCount = sheet.getPhysicalNumberOfRows();
        //判断Excel中数据是否存在
        if (rowCount <= 1) {
            errorMsg.add("Excel中数据不存在，请填写数据");
            return errorMsg;
        }
        //循环读取(从1开始不包含标题行（零行）
        for (int rowNum = 1; rowNum < rowCount; rowNum++) {

            HSSFRow rowData = sheet.getRow(rowNum);

            String levelOneCellValue = "";
            if (rowData != null) {

                //读取一级分类  (零列)
                HSSFCell levelOneCell = rowData.getCell(0);
                levelOneCellValue = excelHSSFUtil.getCellValue(levelOneCell);
                if (levelOneCell != null) {
                    //使用工具类中的方法
                    if (StringUtils.isEmpty(levelOneCellValue)) {
                        errorMsg.add("第" + rowNum + "行一级分类是空的，请重新填写");
                        continue;//当读出该行错误时，继续往后读。而不是停止向后读
                    }
                }

                // 判断一级分类是否重复
                Subject byTitle = this.getByTitle(levelOneCellValue);
                String parentId = null;
                if (byTitle == null) {
                    Subject subjectLevelOne = new Subject();
                    subjectLevelOne.setTitle(levelOneCellValue);
                    subjectLevelOne.setSort(rowNum);
                    // 将一级分类存入数据库
                    baseMapper.insert(subjectLevelOne);//添加
                    parentId = subjectLevelOne.getId();//
                } else {
                    parentId = byTitle.getId();
                }

                // 获取二级分类
                Cell levelTwoCell = rowData.getCell(1);
                String levelTwoCellValue = null;
                if (levelTwoCell != null) {
                    levelTwoCellValue = excelHSSFUtil.getCellValue(levelTwoCell).trim();//修剪掉空白部分
                    if (StringUtils.isEmpty(levelTwoCellValue)) {
                        errorMsg.add("第" + rowNum + "行二级分类是空的，请重新填写");
                        continue;//当读出该行错误时，继续往后读。而不是停止向后读
                    }
                }

                // 判断二级分类是否重复
                Subject subByTitle = this.getSubByTitle(levelTwoCellValue, parentId);
                // 将一级分类获取的ID作为二级分类数据的parentID存储
                if (subByTitle == null) {
                    // 将二级分类存入数据库
                    Subject subjectLevelTwo = new Subject();
                    subjectLevelTwo.setTitle(levelTwoCellValue);
                    subjectLevelTwo.setParentId(parentId);
                    subjectLevelTwo.setSort(rowNum);
                    // 将二级分类存入数据库
                    baseMapper.insert(subjectLevelTwo);//添加
                }
            }
        }
        return errorMsg;
    }

    @Override
    public List<SubjectNestedVo> nestedList() {
        //最终要的到的数据列表
        ArrayList<SubjectNestedVo> subjectNestedVoArrayList = new ArrayList<>();

        //获取一级分类数据记录
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", 0);
        queryWrapper.orderByAsc("sort", "id");
        List<Subject> subjects = baseMapper.selectList(queryWrapper);

        //获取二级分类数据记录
        QueryWrapper<Subject> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.ne("parent_id", 0);
        queryWrapper2.orderByAsc("sort", "id");
        List<Subject> subSubjects = baseMapper.selectList(queryWrapper2);

        //填充一级分类vo数据
        int count = subjects.size();
        for (int i = 0; i < count; i++) {
            Subject subject = subjects.get(i);

            //创建一级类别vo对象
            SubjectNestedVo subjectNestedVo = new SubjectNestedVo();
            BeanUtils.copyProperties(subject, subjectNestedVo);
            subjectNestedVoArrayList.add(subjectNestedVo);

            //填充二级分类vo数据
            ArrayList<SubjectVo> subjectVoArrayList = new ArrayList<>();
            int count2 = subSubjects.size();
            for (int j = 0; j < count2; j++) {

                Subject subSubject = subSubjects.get(j);
                if(subject.getId().equals(subSubject.getParentId())){

                    //创建二级类别vo对象
                    SubjectVo subjectVo = new SubjectVo();
                    BeanUtils.copyProperties(subSubject, subjectVo);
                    subjectVoArrayList.add(subjectVo);
                }
            }
            subjectNestedVo.setChildren(subjectVoArrayList);
        }

        return subjectNestedVoArrayList;
    }


    /**
     * 根据分类名称查询这个一级分类中否存在
     *
     * @param title
     * @return 插入数据库中的内容
     */
    private Subject getByTitle(String title) {

        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", title);
        queryWrapper.eq("parent_id", "0");//一级分类
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据分类名称和父id查询这个二级分类中否存在
     *
     * @param title
     * @return
     */
    private Subject getSubByTitle(String title, String parentId) {

        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", title);
        queryWrapper.eq("parent_id", parentId);
        return baseMapper.selectOne(queryWrapper);
    }
}
