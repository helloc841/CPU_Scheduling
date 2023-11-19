package Service;
import pojo.Pojo_PCB;
public class Service {
    static int count = 0;
    public static void setPriority(Pojo_PCB pojo_pcb){
        int priority = (pojo_pcb.getRuntime() - 3) / 2;
       pojo_pcb.setPriority(priority);
    }
    public static void setRuntime(Pojo_PCB pojo_pcb){
        //随机分配10-15的运行时间
        int runtime =(int)(5 + (Math.random() * 6));
        count++;
        pojo_pcb.setRuntime(runtime);
    }
    public static void setStorage(Pojo_PCB pojo_pcb){
        int Storage = (int) (8 + (Math.random() * 6));
        Storage = Storage * 10;
        pojo_pcb.setRoomOfStore(Storage);
    }
}
