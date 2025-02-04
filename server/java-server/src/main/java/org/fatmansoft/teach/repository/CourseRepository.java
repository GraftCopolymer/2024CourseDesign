package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Course 数据操作接口，主要实现Course数据的查询操作
 */

@Repository
public interface CourseRepository extends JpaRepository<Course,Integer> {
    @Query(value = "from Course where ?1='' or num like %?1% or name like %?1% ")
    List<Course> findCourseListByNumName(String numName);

    Optional<Course> findByNum(String num);
    List<Course> findByName(String name);

    //根据课程ID来查找所有选择了该课程的学生
    @Query("select c.students from Course c where c.courseId=?1")
    List<Student> findStudentsByCourseId(Integer courseId);

    @Query("select c from Course c where c.preCourse.courseId=?1")
    List<Course> findCoursesByPreCourseId(Integer preCourseId);

    //获取某个老师教授的所有课程
    @Query("select c from Course c where c.teacher.teacherId=?1")
    List<Course> findCoursesByTeacherId(Integer teacherId);
}
