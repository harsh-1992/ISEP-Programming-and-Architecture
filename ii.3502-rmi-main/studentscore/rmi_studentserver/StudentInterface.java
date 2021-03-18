package rmi_studentserver;
import java.rmi.RemoteException;
import java.util.List;

public interface StudentInterface extends java.rmi.Remote {
	public String getName() throws RemoteException;
	public int getAge() throws RemoteException;
	public int getId() throws RemoteException;
	public List<Double> getScore() throws RemoteException;
	
	public void add_exams(String name, double score, double coeff) throws RemoteException;
	public String print_exams() throws RemoteException;
	public double calculate_average() throws RemoteException;

}
