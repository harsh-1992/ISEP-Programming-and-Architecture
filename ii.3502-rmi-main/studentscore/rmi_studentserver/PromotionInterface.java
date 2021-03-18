package rmi_studentserver;

import java.rmi.RemoteException;

public interface PromotionInterface extends java.rmi.Remote {
	public void add_student(String name, int age, int id) throws RemoteException;
	public void add_student(Student s) throws RemoteException;
	public Object find_student(int id) throws RemoteException;
	public double promotion_score() throws RemoteException;
}
