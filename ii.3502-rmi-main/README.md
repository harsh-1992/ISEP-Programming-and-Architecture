# ii.3502-rmi

For more convenience, instead of creating multiple projects for each part, I chose to create packages for each part:
- *Part I: HelloWorld Example* is associated to `rmi_serverside` and `rmi_clientside` 
- *Part II: Student scores* is associated to `studentscore.rmi_client` and `studentscore.rmi_server`

Project specifications: Eclipse version 4.10.0 and JDK 13.0.1

On Windows, to find the PID of an application, run the following command line on a terminal: `netstat -a -o –n`. Our applications should run on localhost, port 12345. Finding the PID allows you to force exit on the application using the specified port and address. This is necessary to re-run a new instance of server if the previous one is not closed.

## Part I: HelloWorld Example
This part mainly consists in running the given code.

Minor modifications made on the code on my part: as I chose to group all the given projects into one project, and as they were then dispatched into different packages, the `HelloInterface` file was duplicated. Deleting the interface implementation on the Client side allows us to run both Client and Server properly.

Client-side calls to `HelloInterface` refer to the server-side implementation of `HelloInterface`. I'm aware that when splitting the projects, I'd need to explicit the implementation of the interface.

## Part II: Student scores
In this part, we have to implement an RMI Server part and a Client.

### Student object (`rmi_studentserver.Student`)
The `Student` class implements `StudentInterface`. This interface defines a few methods, including:
- `add_exams`, adding an exam score. An exam is defined by a Subject description, a Score, and a Coefficient between 0 and 1.
- `print_exams`, returning a String containing the scores of all exams registered by the student
- `calculate_average`, computing all the scores of the student and affecting them their coefficients to return the average score of the student.

`Student` extends `UnicastRemoteObject`, which allows `Student` instances to be saved in the Registry. This is necessary to register saves of `Student` instances implemented in the RMI Server.

### RMI Server (`rmi_studentserver.StudentServer`)
RMI Server has to implement methods to:
- Add a student (`add_student`)
- Find a student (`get_student`)
- Calculate the average score of all the students (`promotion_score`)

The methods are specified in the `PromotionInterface` and implemented in the `StudentServer`. `StudentServer` also extends `UniCastRemoteObject`, which allows the Server object to be saved in the RMI Registry using `java.rmi.registry.LocateRegistry.createRegistry` to start a registry at the given address, and `Naming.rebind` to register an object and give it a unique uniform resource identifier (URI).

In the `main` method of `StudentServer`, we register an instance of `StudentServer` in the Registry so we can access it from the Client side.

The `add_student` method is defined twice so we can add student using a `Student` instance or using its constructor.  
The `get_student` method is defined to search through the list of students using their id.  
The `promotion_score` method sums the average score of all the students in the list and divide it by the number of students

### RMI Client (`rmi_client.StudentClient`)
To use the methods defined by `PromotionInterface` and `StudentInterface`, the client has to extract objects implementing those interfaces.

In the registry, from the Server, we registered an `StudentServer` object. We can retrieve it using `Naming.lookup` and the URI previously used. By casting the retrieved object from the RMI Registry to a `PromotionInterface` implementing object, we can use the methods defined in `PromotionInterface`.

This in particular gives access to `add_student` and `find_student`. Adding a student and finding it gives access to the `StudentInterface` methods, casting the previously found students (using `find_student`) to `StudentInterface` implementing objects. This in particular allows us to register new exams for students.

After registering exams for students, we can calculate the student average score and the average score of the promotion.
