package rmi_studentserver;

import java.io.Serializable;

public class Exam implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Exam(String name, double score, double coeff)
	{
		this.name = name;
		this.score = score;
		this.coeff = coeff;
	}
	
	@Override
	public String toString()
	{
		return String.format("%s - %s x %s", name, score, coeff);
	}
	
	private String name;
	private double score;
	private double coeff;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public double getCoeff() {
		return coeff;
	}
	public void setCoeff(double coeff) {
		this.coeff = coeff;
	}
	

}
