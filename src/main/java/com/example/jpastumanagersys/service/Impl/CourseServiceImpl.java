package com.example.jpastumanagersys.service.Impl;

import com.example.jpastumanagersys.entity.Course;
import com.example.jpastumanagersys.entity.User;
import com.example.jpastumanagersys.repo.CourseRepo;
import com.example.jpastumanagersys.repo.UserRepo;
import com.example.jpastumanagersys.service.CourseService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private UserRepo userRepo;

    @Override
    public Course saveCourse(Course course) {
        return courseRepo.save(course);
    }

    @Override
    public Course updateCourse(Course course) {
        Course existingCourse = courseRepo.findByCourseCode(course.getCourseCode())
                .orElseThrow(() -> new IllegalArgumentException("更新课程信息时，没有找到对应编号的课程"));

        // 更新课程名称
        existingCourse.setCourseName(course.getCourseName());

        // 处理课程老师的更新
        User oldTeacher = existingCourse.getTeacher();
        User newTeacher = course.getTeacher();

        if (oldTeacher != null && (newTeacher == null || !oldTeacher.getId().equals(newTeacher.getId()))) {
            // 移除旧老师的课程关联
            oldTeacher.getCourses().remove(existingCourse);
            userRepo.save(oldTeacher);
        }

        if (newTeacher != null && (oldTeacher == null || !newTeacher.getId().equals(oldTeacher.getId()))) {
            // 添加新老师的课程关联
            newTeacher.getCourses().add(existingCourse);
            userRepo.save(newTeacher);
        }

        existingCourse.setTeacher(newTeacher);

        return courseRepo.save(existingCourse);
    }

    @Override
    @Transactional
    public void deleteByCourseCodes(List<String> courseCodes) {
        for (String courseCode : courseCodes) {
            if (hasTeacher(courseCode)) {
                throw new IllegalStateException("课程 " + courseCode + " 已有老师，无法删除");
            }
        }
        courseRepo.deleteAllByCourseCodeIn(courseCodes);
    }

    @Override
    public Course getByCourseCode(String courseCode) {
        return courseRepo.findByCourseCode(courseCode).orElseThrow(() -> new IllegalArgumentException("获取课程信息时，没有找到对应的课程"));
    }

    @Override
    public Page<Course> getAllCourses(Pageable pageable) {
        List<Course> allCourses = courseRepo.findAll();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allCourses.size());

        List<Course> coursePageContent = allCourses.subList(start, end);
        // 为每个课程设置授课老师信息
        for (Course course : coursePageContent) {
            // 获取该课程对应的老师
            List<User> teachers = userRepo.findByCourses_CourseCode(course.getCourseCode());
            if (!teachers.isEmpty()) {
                course.setTeacher(teachers.get(0));
            } else {
                course.setTeacher(null);
            }
        }

        return new PageImpl<>(coursePageContent, pageable, allCourses.size());
    }

    @Override
    public boolean hasTeacher(String courseCode) {
        Course course = courseRepo.findByCourseCode(courseCode).orElseThrow(() -> new IllegalArgumentException("检查课程下是否有对应老师时，无法根据编号找到该课程"));
        return course.getTeacher() != null;
    }

    @Override
    public Page<Course> getByCourseNameContaining(String courseName, Pageable pageable) {
        List<Course> courses = courseRepo.findByCourseNameContaining(courseName);
        return getPagedCourses(courses, pageable);
    }

    @Override
    public Page<Course> getUnassignedCourses(Pageable pageable) {
        List<Course> unassignedCourses = courseRepo.findByTeacherIsNull();
        return getPagedCourses(unassignedCourses, pageable);
    }

    @Override
    public Page<Course> getByTeacher(User teacher, Pageable pageable) {
        List<Course> courses = courseRepo.findByTeacher(teacher);
        return getPagedCourses(courses, pageable);
    }

    @Override
    public Course getById(Long id) {
        return courseRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("无法根据课程ID获取课程"));
    }

    private Page<Course> getPagedCourses(List<Course> courses, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), courses.size());

        List<Course> coursePageContent = courses.subList(start, end);
        // 为每个课程设置授课老师信息
        for (Course course : coursePageContent) {
            // 获取该课程对应的老师
            List<User> teachers = userRepo.findByCourses_CourseCode(course.getCourseCode());
            if (!teachers.isEmpty()) {
                course.setTeacher(teachers.get(0));
            } else {
                course.setTeacher(null);
            }
        }

        return new PageImpl<>(coursePageContent, pageable, courses.size());
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }
}
