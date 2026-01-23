package com.nadhem.produits.restcontrollers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nadhem.produits.entities.Produit;
import com.nadhem.produits.service.ProduitService;

@RestController
@RequestMapping("/api")
@CrossOrigin

public class ProduitRESTController {

	@Autowired

	ProduitService produitService;

	// @RequestMapping(method = RequestMethod.GET)

	@GetMapping()

	public List<Produit> getAllProduts() {
		return produitService.getAllProduits();

	}

	// @RequestMapping(value="/{id}",method = RequestMethod.GET)

	@GetMapping("/{id}")
	public Produit getProduitById(@PathVariable("id") Long id) {

		return produitService.getProduit(id);
	}

	// @RequestMapping(method = RequestMethod.POST)
	@PostMapping()
	public Produit createProduit(@RequestBody Produit produit) {
		return produitService.saveProduit(produit);
	}

	// @RequestMapping(method = RequestMethod.PUT)
	@PutMapping()
	public Produit updateProduit(@RequestBody Produit produit) {
		return produitService.updateProduit(produit);
	}

	// @RequestMapping(value="/{id}",method = RequestMethod.DELETE)
	@DeleteMapping("/{id}")
	public void deleteProduit(@PathVariable("id") Long id) {
		produitService.deleteProduitById(id);
	}

	// @RequestMapping(value="/prodscat/{idCat}",method = RequestMethod.GET)
	@GetMapping("/prodscat/{idCat}")
	public List<Produit> getProduitsByCatId(@PathVariable("idCat") Long idCat) {
		return produitService.findByCategorieIdCat(idCat);
	}

}
