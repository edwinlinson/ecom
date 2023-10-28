package com.example.demo.Repository;

import com.example.demo.Model.Offers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepo extends JpaRepository<Offers,Integer> {

}
