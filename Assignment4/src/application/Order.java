package application;

import java.time.LocalDate;

import javax.persistence.*;

@Entity
@Table(name = "order_table")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "number")
	private int number;

	@Column(name = "date")
	private LocalDate date;

	@Column(name = "item")
	private String item;

	@Column(name = "price")
	private Double price;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	public Order() {

	}

	public Order(int number, LocalDate date, String item, Double price, Customer customer) {
		super();
		this.number = number;
		this.date = date;
		this.item = item;
		this.price = price;
		this.customer = customer;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	public String toString() {
		return "Order [number=" + number + ", date=" + date + ", item=" + item + ", price=" + price + ", customer="
				+ customer + "]";
	}

}
