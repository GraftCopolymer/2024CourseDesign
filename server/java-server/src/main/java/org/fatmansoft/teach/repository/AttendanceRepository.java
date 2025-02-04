package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Attendance;
import org.fatmansoft.teach.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance,Integer> {

    //根据课程名和学生名查询
    @Query(value = "select a from Attendance a where (?1='' or a.course.name like %?1%) and (?2='' or a.student.person.personName like %?2%) ")
    List<Attendance> findByCourseNameAndStudentName(String courseName, String studentName);

    //获取指定学生ID的考勤信息
    @Query("select a from Attendance a where a.student.studentId=?1 and (?2<=0 or a.course.courseId=?2)")
    List<Attendance> findByStudentIdCourseId(Integer studentId, Integer courseId);

    //获取所有老师是指定老师的课程的考勤信息，还附加了血学生和课程名称筛选功能
    @Query(value = "select a from Attendance a where (?1='' or a.course.name like %?1%) and (?2='' or a.student.person.personName like %?2%) and a.course.teacher.teacherId=?3 ")
    List<Attendance> findByCourseNameAndStudentNameAndTeacherId(String courseName, String studentName, Integer teacherId);
}
