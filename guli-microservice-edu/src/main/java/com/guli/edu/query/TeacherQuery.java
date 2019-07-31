package com.guli.edu.query;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Teacher查询对象" , description = "讲师查询对象封装")
public class TeacherQuery {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "教师名称，模糊查询")
    private String name;

    @ApiModelProperty(value = "头衔 1高级 2首席")
    private Integer level;

     @ApiModelProperty(value = "查询开始时间", example = "2019-01-01 11:11:11")
    private String begin;//注意，这里使用的是String类型，前端传过来的数据无需进行类型转换

     @ApiModelProperty(value = "查询开始时间", example = "2019-01-01 11:11:11")
    private String end;

}
