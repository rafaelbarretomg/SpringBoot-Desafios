
@Entity
@Table(name = "customers")
public class Customer implements Serializabe{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    private String email;
    
    private List<Order> orders;
    
    //add getters setters
}