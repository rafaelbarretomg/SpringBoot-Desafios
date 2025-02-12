

@Entity
@Table(name = "products")
public class Product implements Serializabe{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    private String description;
    
    private BigDecimal price;
    
    //add getters setters
}