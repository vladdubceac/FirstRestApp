package md.vladdubceac.spring_learning.FirstRestApp.util;

public class PersonNotCreatedException extends RuntimeException {
    public PersonNotCreatedException(String msg){
        super(msg);
    }
}
