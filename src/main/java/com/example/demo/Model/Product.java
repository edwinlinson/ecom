package com.example.demo.Model;

import com.example.demo.dto.NoWhitespace;
import com.example.demo.dto.ValidImage;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "Product", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Product implements Serializable {

	@Valid

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private long id;

//	@NotEmpty
////	@NoWhitespace
//	@NotBlank
	private String name;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id",referencedColumnName = "category_id")
	private Category category;

	private double price;
//	@NotEmpty
//	@NoWhitespace
	private String brand;
	private String description;


	@ElementCollection
	@CollectionTable(name = "Product_images",joinColumns = @JoinColumn(name = "product_id"))
	@Column(name = "img_name")
	private Set<String> imageNames = new HashSet<>();


	@Column(name = "quantity")
	private int qty;
}
