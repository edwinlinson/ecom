package com.example.demo.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
public class ProductDTO {


	private long id;

	@NotNull
	@NotBlank(message = "Product Name should not be blank spaces.")
	@NotEmpty
	private String name;

	private int categoryId;
	private double price;

	@NotNull
	@NotBlank(message = "Product brand should not be blank")
	@NotEmpty
	private String brand;

	private String description;


	@Getter
//	@ValidImage
	private Set<String> imageNames;


	private int qty;
}
