package rmi_studentserver;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;


public class StudentServer extends UnicastRemoteObject implements PromotionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Student> students;
	
	protected StudentServer(int id, String name, int age) throws java.rmi.RemoteException {
		super();
		students = new ArrayList<Student>();
		this.add_student(name, age, id);
	}
	protected StudentServer() throws java.rmi.RemoteException
	{
		super();
		students = new ArrayList<Student>();
	}

	public static void main(String[] args) throws RemoteException, MalformedURLException {
//		StudentServer obj = new StudentServer(1,"Roger", 23);
		StudentServer obj = new StudentServer();
		// start RMIRegistry: port 12345
		java.rmi.registry.LocateRegistry.createRegistry(12345);
		
		// register the object
		Naming.rebind("rmi://localhost:12345/student_server", obj);
		System.out.println("StudentServer bound in registry");
	}

	public void add_student(String name, int age, int id) throws RemoteException {
		students.add(new Student(id, name, age));
	}
	
	public void add_student(Student student) throws RemoteException{
		students.add(student);
	}

	public Object find_student(int id)  throws RemoteException {
		if(students.size() == 0 || students == null)
		{
			System.out.println("No student found");
			return null;
		}
		for(Student s : students)
		{
			if(s.getId() == id)
			{
				return s;
			}
		}
		return null;
	}
	public double promotion_score() throws RemoteException {
		double total = 0;
		for(Student s : students)
		{
//			System.out.println("Adding student " + s.getName() + "'s average score (" + s.calculate_average() + ")");
			total += s.calculate_average();
		}
		return total/students.size();
	}

}
