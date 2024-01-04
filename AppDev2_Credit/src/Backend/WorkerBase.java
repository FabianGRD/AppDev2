package Backend;

public class WorkerBase {

    public int id;
    public String firstname;
    public String lastname;

    public String toString(){
        return "" + id +
                " " + firstname +
                " " + lastname;
    }
}
