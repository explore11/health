package com.hr.health.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hr.health.business.domain.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestStudentMapper extends BaseMapper<Student> {
}
