import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Order {
    private String orderId;
    private String strudentId;
    private ArrayList<OrderItem>items;
    private LocalDateTime orDateTime;
    private boolean isPaid;

    
    public Order(String strudentId) {

        this.orderId = "66H"+System.currentTimeMillis();
        this.strudentId = strudentId;
        this.items = new ArrayList<>();
        this.orDateTime = LocalDateTime.now();
        this.isPaid = false;
    }

    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getStrudentId() {
        return strudentId;
    }
    public void setStrudentId(String strudentId) {
        this.strudentId = strudentId;
    }
    public ArrayList<OrderItem> getItems() {
        return items;
    }
    public void setItems(ArrayList<OrderItem> items) {
        this.items = items;
    }
    public LocalDateTime getOrDateTime() {
        return orDateTime;
    }
    public void setOrDateTime(LocalDateTime orDateTime) {
        this.orDateTime = orDateTime;
    }
    public boolean isPaid() {
        return isPaid;
    }
    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }
    


    public void addItem(OrderItem item){
        items.add(item);
    }
    
    public double getToatalAmount(){
        double total=0.0;
        
        for(OrderItem i : items){
            total+=i.getPrice();
        }
        return total;
    }

    public void markAsPaid(){
        this.isPaid=true;
    }

    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append(orderId).append(",");
        sb.append(strudentId).append(",");

        sb.append(orDateTime.format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss"))).append(",");
        sb.append(isPaid);
        for (OrderItem orderItem : items) {
            sb.append(",").append(orderItem.toString());
        }
        return sb.toString();
    }

        public static Order fromString(String str, ArrayList<FoodItem> menu) {
        String[] parts = str.split(";");
        String[] mainParts = parts[0].split(",");

        Order order = new Order(mainParts[1]);
        order.orderId = mainParts[0];
        order.orDateTime = LocalDateTime.parse(mainParts[2], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        order.isPaid = Boolean.parseBoolean(mainParts[3]);

        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                order.addItem(OrderItem.fromString(parts[i], menu));
            }
        }

        return order;
    }
}
