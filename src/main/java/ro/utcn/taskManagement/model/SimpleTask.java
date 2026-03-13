package ro.utcn.taskManagement.model;

public final class SimpleTask extends Task{
    private int startHour;
    private int endHour;
    public SimpleTask(String statusTask, int idTask, int startHour, int endHour){
        super(idTask, statusTask);
        this.startHour = startHour;
        this.endHour = endHour;
    }
    public int estimateDuration(){
        if(startHour > endHour){return endHour+24-startHour;}
        return endHour - startHour;
    }

    public String toString(){
        return "Simple Task: "+super.toString()+", Hours: "+startHour+" - "+endHour;
    }
}
