package exam.model.entity;

import exam.util.Messages;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "shops")
public class Shop extends BaseEntity{

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private BigDecimal income;

    @Column(nullable = false)
    private String address;

    @Column(name = "employee_count", nullable = false)
    private int employeeCount;

    @Column(name = "shop_area", nullable = false)
    private int shopArea;

    @OneToOne
    private Town town;

    @Override
    public String toString() {
        return name + Messages.DASH + income.toString();
    }
}