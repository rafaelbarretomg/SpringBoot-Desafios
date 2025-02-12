

@Entity
@Table(name = "order-items")
public class Order implements Serializabe{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Order order;
    
    private Product product;
    
    private Integer quantity;
    
    private BigDecimal price;
    
}