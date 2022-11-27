package exam.model.entity;

import exam.util.Messages;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "laptops")
public class Laptop extends BaseEntity{

    @Column(name = "mac_address", nullable = false, unique = true)
    private String macAddress;

    @Column(name = "cpu_speed", nullable = false)
    private double cpuSpeed;

    @Column
    private int ram;

    @Column
    private int storage;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column
    private BigDecimal price;

    @Enumerated
    @Column(name = "warranty_type", nullable = false)
    private WarrantyType warrantyType;

    @ManyToOne(optional = false)
    private Shop shop;

    @Override
    public String toString() {
        //Laptop - {mac address}
//        *Cpu speed - {cpu speed}
//        **Ram - {ram}
//        ***Storage - {storage}
//        ****Price - {price}
//        #Shop name - {name of the shop}
//        ##Town - {the name of the town of shop}
        return String.format(Messages.PRINT_LAPTOP, macAddress) + System.lineSeparator() +
             String.format(Messages.PRINT_CPU, cpuSpeed) + System.lineSeparator() +
             String.format(Messages.PRINT_RAM, ram) + System.lineSeparator() +
             String.format(Messages.PRINT_STORAGE, storage) + System.lineSeparator() +
             String.format(Messages.PRINT_PRICE, price) + System.lineSeparator() +
             String.format(Messages.PRINT_SHOP, shop.getName()) + System.lineSeparator() +
             String.format(Messages.PRINT_TOWN, shop.getTown().getName()) + System.lineSeparator();
    }

    public String importInfo(){
        return macAddress + Messages.DASH +
                String.format(Messages.FORMAT_DOUBLE, cpuSpeed) + Messages.DASH +
                ram + Messages.DASH +
                storage;
    }
}
