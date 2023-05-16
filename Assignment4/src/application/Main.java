package application;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.type.IntegerType;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {

	private SessionFactory sessionFactory;
	private Session session;
	private Customer temp;
	private Order tempo;

	@Override
	public void start(Stage primaryStage) throws Exception {

		Configuration configuration = new Configuration().configure();
		sessionFactory = configuration.buildSessionFactory();
		session = sessionFactory.openSession();

		// Customer GUI
		Label nameLabel = new Label("Name");
		TextField nameField = new TextField();
		Label phoneLabel = new Label("Phone");
		TextField phoneField = new TextField();
		phoneField.setPrefWidth(200);
		Label emailLabel = new Label("Email");
		TextField emailField = new TextField();
		emailField.setPrefWidth(400);
		VBox vb = new VBox(10, phoneLabel, phoneField);
		VBox vb2 = new VBox(10, emailLabel, emailField);
		HBox hb = new HBox(20, vb, vb2);

		Label addressLabel = new Label("Address");
		Label streetLabel = new Label("Street");
		TextField streetField = new TextField();
		streetField.setPrefWidth(210);
		Label cityLabel = new Label("City");
		TextField cityField = new TextField();
		cityField.setPrefWidth(210);
		Label stateLabel = new Label("Stat");
		TextField stateField = new TextField();
		stateField.setPrefWidth(210);
		Label zipcodeLabel = new Label("Zipcode");
		TextField zipcodeField = new TextField();
		zipcodeField.setPrefWidth(210);
		VBox vba = new VBox(10, streetLabel, streetField, stateLabel, stateField);
		VBox vba2 = new VBox(10, cityLabel, cityField, zipcodeLabel, zipcodeField);
		vba.setPadding(new Insets(10));
		vba2.setPadding(new Insets(10));

		TilePane ads = new TilePane(vba, vba2);
		ads.setHgap(80);
		ads.setPadding(new Insets(10));
		ads.setPrefColumns(2);
		ads.setPrefRows(1);

		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(ads);
		borderPane.setStyle("-fx-border-color: lightgray; -fx-border-width: 2px;");

		Separator separator = new Separator();
		separator.setPrefWidth(600);
		separator.setStyle("-fx-background-color: lightgray;");

		Button searchButton = new Button("Search");
		Button addButton = new Button("Add");
		Button updateButton = new Button("Update");
		Button deleteButton = new Button("Delete");
		HBox buttons = new HBox(20, searchButton, addButton, updateButton, deleteButton);
		buttons.setPadding(new Insets(0, 10, 0, 0));
		buttons.setAlignment(Pos.BASELINE_RIGHT);

		VBox data = new VBox(10, nameLabel, nameField, hb, addressLabel, borderPane);
		data.setPadding(new Insets(10));
		VBox sep = new VBox(10, buttons, separator);
		VBox mainc = new VBox(data, sep);
		Tab customerTab = new Tab("Customer", mainc);

		// Order Tab GUI
		Label number = new Label("Number");
		Label date = new Label("Date");
		TextField numberField = new TextField();
		numberField.setPromptText("auto-generated");
		TextField dateField = new TextField();
		dateField.setPromptText("YYYY-MM-DD");
		VBox num = new VBox(10, number, numberField);
		VBox da = new VBox(10, date, dateField);
		HBox hbo = new HBox(300, num, da);

		Label customerLabel = new Label("Customer");
		ComboBox<Customer> customerView = new ComboBox<>();
		customerView.setItems(FXCollections.observableArrayList(retrieveCustomers()));
		customerView.setPrefWidth(600);
		Label itemLabel = new Label("Item");
		ComboBox<String> itemView = new ComboBox<>();
		itemView.setItems(FXCollections.observableArrayList("Caesar Salad", "Greek Salad", "Cobb Salad"));
		itemView.setPrefWidth(300);
		VBox cus = new VBox(10, customerLabel, customerView);
		VBox ite = new VBox(10, itemLabel, itemView);
		Label priceLabel = new Label("Price ($)");
		TextField priceField = new TextField();
		priceField.setPromptText("format (e.g., 1.99)");
		VBox pric = new VBox(10, priceLabel, priceField);
		HBox itpric = new HBox(150, ite, pric);

		Button searchButton2 = new Button("Search");
		Button addButton2 = new Button("Add");
		Button updateButton2 = new Button("Update");
		Button deleteButton2 = new Button("Delete");
		HBox buttons2 = new HBox(20, searchButton2, addButton2, updateButton2, deleteButton2);
		buttons2.setAlignment(Pos.BASELINE_RIGHT);
		buttons2.setPadding(new Insets(103, 10, 0, 0));

		VBox data2 = new VBox(20, hbo, cus, itpric);
		data2.setPadding(new Insets(10));
		Separator separator2 = new Separator();
		separator2.setPrefWidth(600);
		separator2.setStyle("-fx-background-color: lightgray;");
		VBox maino = new VBox(10, data2, buttons2, separator2);
		Tab orderTab = new Tab("Order", maino);

		TabPane tabPane = new TabPane();
		tabPane.setTabMinWidth(280);
		tabPane.setTabMaxWidth(280);
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		tabPane.getTabs().addAll(customerTab, orderTab);

		// Create the Scene and set it to the Stage
		Scene scene = new Scene(tabPane, 600, 400);
		primaryStage.setScene(scene);
		primaryStage.show();

		// All button functionality and alerts
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		searchButton.setOnAction(event -> {
			if (!nameField.getText().isBlank()) {
				Query<Customer> query = session.createQuery("FROM Customer WHERE name = :name", Customer.class);
				query.setParameter("name", nameField.getText());
				List<Customer> customers = query.getResultList();
				if (customers.isEmpty()) {
					temp = null;
					alert.setHeaderText("Search Result");
					alert.setContentText("Customer not found!");
					alert.showAndWait();
				} else if (customers.size() == 1) {
					temp = customers.get(0);
					nameField.setText(temp.getName());
					phoneField.setText(temp.getPhone());
					emailField.setText(temp.getEmail());
					cityField.setText(temp.getAddress().getCity());
					stateField.setText(temp.getAddress().getState());
					zipcodeField.setText(
							temp.getAddress().getZipCode() == null ? "" : temp.getAddress().getZipCode().toString());
					streetField.setText(temp.getAddress().getStreet());
					alert.setHeaderText("Search Result");
					alert.setContentText("Customer found!");
					alert.showAndWait();
				} else {
					ListView<Customer> tempView = new ListView<>(FXCollections.observableArrayList(customers));
					alert.setTitle("Search Result");
					alert.setHeaderText("Multiple customers with the same name!\nSelect one:");
					alert.getDialogPane().setContent(tempView);
					alert.setOnCloseRequest(eventa -> {
						if (alert.getResult() == ButtonType.OK) {
							temp = tempView.getSelectionModel().getSelectedItem();
							nameField.setText(temp.getName());
							phoneField.setText(temp.getPhone());
							emailField.setText(temp.getEmail());
							cityField.setText(temp.getAddress().getCity());
							stateField.setText(temp.getAddress().getState());
							zipcodeField.setText(temp.getAddress().getZipCode() == null ? ""
									: temp.getAddress().getZipCode().toString());
							streetField.setText(temp.getAddress().getStreet());
						}
					});
					alert.showAndWait();
					alert.getDialogPane().setContent(null);
				}
				customerView.setItems(FXCollections.observableArrayList(retrieveCustomers()));
			} else {
				alert.setHeaderText("Error");
				alert.setContentText("Search name blank!");
				alert.showAndWait();
			}
		});
		addButton.setOnAction(event -> {
			try {
				if (!nameField.getText().isBlank() && !phoneField.getText().isBlank()
						&& !streetField.getText().isBlank() && !cityField.getText().isBlank()
						&& !stateField.getText().isBlank()) {
					Customer newCustomer = new Customer();
					Query<Customer> query = session.createQuery("FROM Customer WHERE phone = :phone", Customer.class);
					query.setParameter("phone", phoneField.getText());
					List<Customer> identical = query.getResultList();

					if (identical.isEmpty()) {

						newCustomer.setPhone(phoneField.getText());
						Query<Customer> query2 = session.createQuery("FROM Customer WHERE email = :email",
								Customer.class);
						query2.setParameter("email", emailField.getText());
						List<Customer> identical2 = query2.getResultList();

						if (identical2.isEmpty()) {

							newCustomer.setEmail(emailField.getText());
							Query<Address> query3 = session.createQuery(
									"FROM Address WHERE street = :street AND city = :city AND state = :state AND zip_code = :zip_code",
									Address.class);
							query3.setParameter("street", streetField.getText());
							query3.setParameter("city", cityField.getText());
							query3.setParameter("state", stateField.getText());
							query3.setParameter("zip_code",
									zipcodeField.getText().isBlank() ? null : Integer.parseInt(zipcodeField.getText()),
									IntegerType.INSTANCE);

							List<Address> identical3 = query3.getResultList();

							if (identical3.isEmpty()) {
								Address newAddress = new Address();
								newAddress.setStreet(streetField.getText());
								newAddress.setCity(cityField.getText());
								newAddress.setState(stateField.getText());
								newAddress.setZipCode(zipcodeField.getText().isBlank() ? null
										: Integer.parseInt(zipcodeField.getText()));

								Transaction transaction = session.beginTransaction();
								session.save(newAddress);
								transaction.commit();

								newCustomer.setAddress(newAddress);
								newCustomer.setName(nameField.getText());

								Transaction transaction2 = session.beginTransaction();
								session.save(newCustomer);
								transaction2.commit();

								nameField.clear();
								phoneField.clear();
								emailField.clear();
								cityField.clear();
								stateField.clear();
								zipcodeField.clear();
								streetField.clear();

								alert.setHeaderText("Notice");
								alert.setContentText("Customer added!");
								alert.showAndWait();
							} else {
								newCustomer.setAddress(identical3.get(0));
								newCustomer.setName(nameField.getText());

								Transaction transaction = session.beginTransaction();
								session.save(newCustomer);
								transaction.commit();

								nameField.clear();
								phoneField.clear();
								emailField.clear();
								cityField.clear();
								stateField.clear();
								zipcodeField.clear();
								streetField.clear();

								alert.setHeaderText("Notice");
								alert.setContentText("Customer added!");
								alert.showAndWait();
							}
						} else {
							alert.setHeaderText("Error");
							alert.setContentText("Email taken!");
							alert.showAndWait();
						}
					} else {
						alert.setHeaderText("Error");
						alert.setContentText("Phone number taken!");
						alert.showAndWait();
					}
					customerView.setItems(FXCollections.observableArrayList(retrieveCustomers()));
				} else {
					alert.setHeaderText("Error");
					alert.setContentText("Some fields are blank!");
					alert.showAndWait();
				}

			} catch (NumberFormatException e) {
				alert.setHeaderText("Error");
				alert.setContentText("Zip code invalid!");
				alert.showAndWait();
			}

		});
		updateButton.setOnAction(event -> {
			try {

				if (temp != null) {

					if (!nameField.getText().isBlank() && !phoneField.getText().isBlank()
							&& !streetField.getText().isBlank() && !cityField.getText().isBlank()
							&& !stateField.getText().isBlank()) {
						Query<Customer> query = session.createQuery("FROM Customer WHERE phone = :phone",
								Customer.class);
						query.setParameter("phone", phoneField.getText());
						List<Customer> identical = query.getResultList();

						if (identical.isEmpty() || identical.get(0) == temp) {

							temp.setPhone(phoneField.getText());
							Query<Customer> query2 = session.createQuery("FROM Customer WHERE email = :email",
									Customer.class);
							query2.setParameter("email", emailField.getText());
							List<Customer> identical2 = query2.getResultList();

							if (identical2.isEmpty() || identical2.get(0) == temp) {

								temp.setEmail(emailField.getText());
								Query<Address> query3 = session.createQuery(
										"FROM Address WHERE street = :street AND city = :city AND state = :state AND zip_code = :zip_code",
										Address.class);
								query3.setParameter("street", streetField.getText());
								query3.setParameter("city", cityField.getText());
								query3.setParameter("state", stateField.getText());
								query3.setParameter("zip_code", zipcodeField.getText().isBlank() ? null
										: Integer.parseInt(zipcodeField.getText()), IntegerType.INSTANCE);
								List<Address> identical3 = query3.getResultList();

								if (identical3.isEmpty()) {
									Address newAddress = new Address();
									newAddress.setStreet(streetField.getText());
									newAddress.setCity(cityField.getText());
									newAddress.setState(stateField.getText());
									newAddress.setZipCode(zipcodeField.getText().isBlank() ? null
											: Integer.parseInt(zipcodeField.getText()));

									Transaction transaction = session.beginTransaction();
									session.save(newAddress);
									transaction.commit();

									temp.setAddress(newAddress);
									temp.setName(nameField.getText());

									Transaction transaction2 = session.beginTransaction();
									session.update(temp);
									transaction2.commit();

									nameField.clear();
									phoneField.clear();
									emailField.clear();
									cityField.clear();
									stateField.clear();
									zipcodeField.clear();
									streetField.clear();
									temp = null;

									alert.setHeaderText("Notice");
									alert.setContentText("Customer updated!");
									alert.showAndWait();
								} else {
									temp.setAddress(identical3.get(0));
									temp.setName(nameField.getText());

									Transaction transaction = session.beginTransaction();
									session.update(temp);
									transaction.commit();

									nameField.clear();
									phoneField.clear();
									emailField.clear();
									cityField.clear();
									stateField.clear();
									zipcodeField.clear();
									streetField.clear();
									temp = null;

									alert.setHeaderText("Notice");
									alert.setContentText("Customer updated!");
									alert.showAndWait();
								}
							} else {
								alert.setHeaderText("Error");
								alert.setContentText("Email taken!");
								alert.showAndWait();
							}
						} else {
							alert.setHeaderText("Error");
							alert.setContentText("Phone number taken!");
							alert.showAndWait();
						}
						customerView.setItems(FXCollections.observableArrayList(retrieveCustomers()));
					} else {
						alert.setHeaderText("Error");
						alert.setContentText("Some fields are blank!");
						alert.showAndWait();
					}

				} else {
					alert.setHeaderText("Error");
					alert.setContentText("Search a customer first to update!");
					alert.showAndWait();
				}
			} catch (NumberFormatException e) {
				alert.setHeaderText("Error");
				alert.setContentText("Zip code invalid!");
				alert.showAndWait();
			}
		});
		deleteButton.setOnAction(event -> {

			if (temp != null) {

				Transaction transaction = session.beginTransaction();
				session.delete(temp);
				session.delete(temp.getAddress());
				transaction.commit();
				customerView.setItems(FXCollections.observableArrayList(retrieveCustomers()));

				nameField.clear();
				phoneField.clear();
				emailField.clear();
				cityField.clear();
				stateField.clear();
				zipcodeField.clear();
				streetField.clear();
				temp = null;

				alert.setHeaderText("Notice");
				alert.setContentText("Customer deleted!");
				alert.showAndWait();
			} else {
				alert.setHeaderText("Error");
				alert.setContentText("Search a customer first to delete!");
				alert.showAndWait();
			}
		});
		searchButton2.setOnAction(event -> {
			if (!numberField.getText().isBlank()) {
				Query<Order> query = session.createQuery("FROM Order WHERE number = :number", Order.class);
				query.setParameter("number", Integer.parseInt(numberField.getText()));
				List<Order> orders = query.getResultList();
				if (orders.isEmpty()) {
					tempo = null;
					alert.setHeaderText("Search Result");
					alert.setContentText("Order not found!");
					alert.showAndWait();
				} else {
					tempo = orders.get(0);
					dateField.setText(tempo.getDate().toString());
					priceField.setText(tempo.getPrice().toString());
					customerView.setValue(tempo.getCustomer());
					itemView.setValue(tempo.getItem());
					alert.setHeaderText("Search Result");
					alert.setContentText("Order found!");
					alert.showAndWait();
				}
			} else {
				alert.setHeaderText("Error");
				alert.setContentText("Search name blank!");
				alert.showAndWait();
			}
		});
		addButton2.setOnAction(event -> {

			try {
				if (!dateField.getText().isBlank() && !priceField.getText().isBlank()
						&& !customerView.getSelectionModel().isEmpty() && !itemView.getSelectionModel().isEmpty()) {
					Order newOrder = new Order();

					newOrder.setDate(LocalDate.parse(dateField.getText()));
					newOrder.setCustomer(customerView.getSelectionModel().getSelectedItem());
					newOrder.setItem(itemView.getSelectionModel().getSelectedItem());
					newOrder.setPrice(Double.parseDouble(priceField.getText()));

					Transaction transaction = session.beginTransaction();
					session.save(newOrder);
					transaction.commit();

					numberField.clear();
					dateField.clear();
					priceField.clear();
					itemView.getSelectionModel().clearSelection();
					customerView.getSelectionModel().clearSelection();

					alert.setHeaderText("Notice");
					alert.setContentText("Order " + newOrder.getNumber() + " added!");
					alert.showAndWait();
				} else {
					alert.setHeaderText("Error");
					alert.setContentText("Some fields are blank!");
					alert.showAndWait();
				}

			} catch (NumberFormatException e) {
				alert.setHeaderText("Error");
				alert.setContentText("Order number or Price invalid!");
				alert.showAndWait();
			} catch (DateTimeParseException e) {
				alert.setHeaderText("Error");
				alert.setContentText("Date invalid!");
				alert.showAndWait();
			}
		});
		updateButton2.setOnAction(event -> {

			try {
				if (tempo != null) {

					if (!dateField.getText().isBlank() && !priceField.getText().isBlank()
							&& !customerView.getSelectionModel().isEmpty() && !itemView.getSelectionModel().isEmpty()) {
						tempo.setDate(LocalDate.parse(dateField.getText()));
						tempo.setCustomer(customerView.getSelectionModel().getSelectedItem());
						tempo.setItem(itemView.getSelectionModel().getSelectedItem());
						tempo.setPrice(Double.parseDouble(priceField.getText()));

						Transaction transaction = session.beginTransaction();
						session.update(tempo);
						transaction.commit();

						numberField.clear();
						dateField.clear();
						priceField.clear();
						itemView.getSelectionModel().clearSelection();
						customerView.getSelectionModel().clearSelection();

						alert.setHeaderText("Notice");
						alert.setContentText("Order " + tempo.getNumber() + " updated!");
						alert.showAndWait();
						tempo = null;
					} else {
						alert.setHeaderText("Error");
						alert.setContentText("Some fields are blank!");
						alert.showAndWait();
					}

				} else {
					alert.setHeaderText("Error");
					alert.setContentText("Search an order first to update!");
					alert.showAndWait();
				}
			} catch (NumberFormatException e) {
				alert.setHeaderText("Error");
				alert.setContentText("Order number or Price invalid!");
				alert.showAndWait();
			} catch (DateTimeParseException e) {
				alert.setHeaderText("Error");
				alert.setContentText("Date invalid!");
				alert.showAndWait();
			}
		});
		deleteButton2.setOnAction(event -> {

			if (tempo != null) {

				Transaction transaction = session.beginTransaction();
				session.delete(tempo);
				transaction.commit();

				numberField.clear();
				dateField.clear();
				priceField.clear();
				itemView.getSelectionModel().clearSelection();
				customerView.getSelectionModel().clearSelection();

				alert.setHeaderText("Notice");
				alert.setContentText("Order " + tempo.getNumber() + " deleted!");
				alert.showAndWait();
				tempo = null;
			} else {
				alert.setHeaderText("Error");
				alert.setContentText("Search an order first to delete!");
				alert.showAndWait();
			}
		});

	}

	// Closes database session on closure of the program
	@Override
	public void stop() throws Exception {
		session.close();
		sessionFactory.close();
	}

	// Retrieves Customers from database
	private List<Customer> retrieveCustomers() {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Customer> query = builder.createQuery(Customer.class);
			Root<Customer> root = query.from(Customer.class);
			query.select(root);

			return session.createQuery(query).list();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<>(); // Return an empty list if retrieval fails
	}

	public static void main(String[] args) {
		launch(args);
	}
}
