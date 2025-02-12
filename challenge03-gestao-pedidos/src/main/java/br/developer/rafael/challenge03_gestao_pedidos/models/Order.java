

@Entity
@Table(name = "order-items")
public class Order implements Serializabe{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Customer customer;
    
    private List<OrderItem> items;
    
    private BigDecimal totalPrice;
    
        //add getters setters
    
}