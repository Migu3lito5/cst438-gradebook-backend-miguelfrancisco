package com.cst438;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.chrome.ChromeDriver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cst438.domain.Assignment;

import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

import com.cst438.domain.EnrollmentRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


@SpringBootTest
public class EndToEndCreateAssignment {
	public static final String CHROME_DRIVER_FILE_LOCATION = "C:/Users/Miguel F/Downloads/chromedriver_win32 (1)/chromedriver.exe";
	public static final String URL = "http://localhost:3000/add";
	public static final String TEST_USER_EMAIL = "test@csumb.edu";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 4000;
	public static final String TEST_ASSIGNMENT_NAME = "HW1 TESTT";
	public static final String TEST_COURSE_TITLE = "Math ";
	public static final String TEST_STUDENT_NAME = "Bob";
	public static final String TEST_DUE_DATE = "2023-04-23";
	public static final int TEST_COURSE_ID = 123457;
	
	
	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentGradeRepository assignnmentGradeRepository;

	@Autowired
	AssignmentRepository assignmentRepository;
	
	
	@Test
	public void addAssignmentTest() throws Exception { 	
	
		
			List<Assignment> assignmentList = new ArrayList<Assignment>();
			Course c = new Course();
			c.setCourse_id(TEST_COURSE_ID);
			c.setInstructor(TEST_INSTRUCTOR_EMAIL);
			c.setSemester("Fall");
			c.setYear(2021);
			c.setTitle(TEST_COURSE_TITLE);
			courseRepository.save(c);


			System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
			WebDriver driver = new ChromeDriver();
			// Puts an Implicit wait for 10 seconds before throwing exception
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);
			
			
			try {
				driver.findElement(By.id("name")).sendKeys(TEST_ASSIGNMENT_NAME);;
				
				driver.findElement(By.id("duedate")).sendKeys(TEST_DUE_DATE);;
				
				driver.findElement(By.id("course_id")).sendKeys(String.valueOf(TEST_COURSE_ID));
				
				driver.findElement(By.id("submit")).click();
				Thread.sleep(SLEEP_DURATION * 2);
				
				
				
				for(Assignment assign : assignmentRepository.findAll()) {
					if(assign.getCourse().getCourse_id() == TEST_COURSE_ID) {
						assignmentList.add(assign);
					}
				}
			} catch (Exception e) {
				throw e;
			} finally {
				
				// Remove from db and in the test if it was indeed deleted
				for(Assignment assignment : assignmentList) {
					assignmentRepository.delete(assignment);
				}
			
				
				for(Assignment assignment : assignmentRepository.findAll()) {
					assertNotEquals(assignment.getCourse(), c);
				}
				
				courseRepository.delete(c);
				
				
				assertEquals(courseRepository.findById(TEST_COURSE_ID), Optional.empty());
				driver.quit();
			}
		
	
	}
}
