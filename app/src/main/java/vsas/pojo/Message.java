package vsas.pojo;

public class Message {
    Status status;
    String msg;

    public Message(){
       this.status = Status.SUCCESS;
       this.msg = null;
    }

    public Message(Status status, String msg){
        this.status = status;
        this.msg = msg;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public enum Status{
        SUCCESS,
        FAIL,
        WARNING,
        INFO
    }
}
