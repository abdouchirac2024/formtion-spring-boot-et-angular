package com.nadhem.produits.restcontrollers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nadhem.produits.entities.Produit;
import com.nadhem.produits.service.ProduitService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@CrossOrigin


public class ProduitRESTController {
	
	@Autowired
	
     ProduitService produitService;
	
	//@RequestMapping(method = RequestMethod.GET)
	 
	@GetMapping()
	
	public  List<Produit> getAllProduts()
	{
		return produitService.getAllProduits();
		
	}
	
	
	//@RequestMapping(value="/{id}",method = RequestMethod.GET)
	
	@GetMapping("/{id}")
	public Produit getProduitById(@PathVariable("id") Long id) {
		
	return produitService.getProduit(id);
	}
	
	
	//@RequestMapping(method = RequestMethod.POST)
	@PostMapping()
	public Produit createProduit(@RequestBody Produit produit) {
	return produitService.saveProduit(produit);
	}

}
