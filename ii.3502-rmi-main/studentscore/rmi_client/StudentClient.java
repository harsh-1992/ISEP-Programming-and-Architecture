package rmi_client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Arrays;

import rmi_serverside.HelloInterface;
import rmi_studentserver.PromotionInterface;
import rmi_studentserver.StudentInterface;
import rmi_studentserver.StudentServer;


public class StudentClient
{
	public static void main(String args[]) throws RemoteException, MalformedURLException 
	{
		PromotionInterface obj = null;
		try 
		{
			// lookup for the object
			obj = (PromotionInterface)Naming.lookup("rmi://localhost:12345/student_server");
			
			obj.add_student("Gerard", 19, 1);
			obj.add_student("Roger", 23, 2);
			obj.add_student("Lucille", 18, 3);
			obj.add_student("Rose", 26, 4);
			
			// call methods
			StudentInterface student1 = (StudentInterface)obj.find_student(1);
			student1.add_exams("Maths", 20, 0.5);
			student1.add_exams("French", 13, 0.2);
			student1.add_exams("English", 16, 0.2);
			student1.add_exams("Java Programmation", 3, 0.1);
			
			StudentInterface student2 = (StudentInterface)obj.find_student(2);
			student2.add_exams("Maths", 16, 0.5);
			student2.add_exams("French", 12, 0.2);
			student2.add_exams("English", 11, 0.2);
			student2.add_exams("Java Programmation", 17, 0.1);
			
			StudentInterface student3 = (StudentInterface)obj.find_student(3);
			student3.add_exams("Maths", 15, 0.5);
			student3.add_exams("French", 14, 0.2);
			student3.add_exams("English", 16, 0.2);
			student3.add_exams("Java Programmation", 12, 0.1);
			
			StudentInterface student4 = (StudentInterface)obj.find_student(4);
			student4.add_exams("Maths", 14, 0.5);
			student4.add_exams("French", 12, 0.2);
			student4.add_exams("English", 19, 0.2);
			student4.add_exams("Java Programmation", 7, 0.1);
			
			
			System.out.println(student1.print_exams());
			System.out.println(student1.calculate_average());
			System.out.println(student1.getScore());
			
			System.out.println(String.format("Promotion score: %s", obj.promotion_score()));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
//			Naming.rebind("rmi://localhost:12345/student_server", obj);
		}
	}
		// TODO Auto-generated method stub

}
