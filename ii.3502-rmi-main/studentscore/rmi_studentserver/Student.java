package rmi_studentserver;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Student extends UnicastRemoteObject implements StudentInterface, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private int age;
	
	private List<Double> score;
	private List<Exam> exams;
	
	

	public Student(int id, String name, int age) throws RemoteException
	{
		super();
		this.id = id;
		this.name = name;
		this.age = age;
		this.score = new ArrayList<Double>();
		this.exams = new ArrayList<Exam>();
	}
	
	public int getId() throws RemoteException{
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() throws RemoteException {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge()  throws RemoteException
	{
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setScore(List<Double> score) {
		this.score = score;
	}

	public List<Double> getScore()  throws RemoteException{
		return score;
	}

	public void add_exams(String name, double score, double coeff) throws RemoteException {
		exams.add(new Exam(name, score, coeff));
		this.score.add(score);
	}

	@SuppressWarnings("unchecked")
	public String print_exams() throws RemoteException {
		return String.format("(Subject - Score x Coefficient) %s", Arrays.asList(exams).toString());
	}

	public double calculate_average() throws RemoteException {
		double sum = 0;
//		System.out.println("I got " + exams.size() + " exams");
		for(Exam e : exams)
		{
			sum += e.getCoeff() * e.getScore();
		}
		return sum;
	}

}
