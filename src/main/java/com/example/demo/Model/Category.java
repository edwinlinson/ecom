package com.example.demo.Model;


import com.example.demo.dto.NoWhitespace;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "Category")
public class Category implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="category_id")
	private int id;

	@Column(nullable = false, unique = true)
	@NotEmpty
	@NoWhitespace
	private String name;

}
