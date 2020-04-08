package com.ipiecoles.java.java350.model;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class EmployeTest {

	//Test unitaire qui test un seul cas
	@Test //Junit 5 import : avec jupiter soit Junit 5 : org.junit.jupiter.api.Test
	public void testAncienneteDateEmbaucheNmoins2() {
		
		//Employé dateEmbauche avec date 2ans avant aujourd'hui => 
		//2 années d'ancienneté
		
			//Given = Initialisation des données d'entrée
			Employe employe = new Employe();
			employe.setDateEmbauche(LocalDate.now().minusYears(2));
			
			//When = Exécution de la méthode à tester
			Integer nbAnnees = employe.getNombreAnneeAnciennete();
			
			//Then = Vérifications de ce qu'a fait la méthode
			Assertions.assertThat(nbAnnees).isEqualTo(2);
			//AserJ pour faire des assertions bcp plus lisible que Junit
	}
	
	@Test
	public void testAncienneteDateEmbaucheNplus2() {
		
		//Employé dateEmbauche avec date 2ans après aujourd'hui => 
		//0 années d'ancienneté
		
			//Given = Initialisation des données d'entrée
			Employe employe = new Employe();
			employe.setDateEmbauche(LocalDate.now().plusYears(2));
			
			//When = Exécution de la méthode à tester
			Integer nbAnnees = employe.getNombreAnneeAnciennete();
			
			//Then = Vérifications de ce qu'a fait la méthode
			Assertions.assertThat(nbAnnees).isEqualTo(0);
			//AserJ pour faire des assertions bcp plus lisible que Junit
	}
	
	//Date aujourd'hui => 0
	@Test
	public void testAncienneteDateEmbaucheAujourdhui() {
		
		//Employé dateEmbauche avec date d'aujourd'hui => 
		//0 années d'ancienneté
		
			//Given = Initialisation des données d'entrée
			Employe employe = new Employe();
			employe.setDateEmbauche(LocalDate.now());
			
			//When = Exécution de la méthode à tester
			Integer nbAnnees = employe.getNombreAnneeAnciennete();
			
			//Then = Vérifications de ce qu'a fait la méthode
			Assertions.assertThat(nbAnnees).isEqualTo(0);
			//AserJ pour faire des assertions bcp plus lisible que Junit
	}
	
	//Date d'embauche indéfinie => 0 (on considère que c 0 dans ce cas)
	@Test
	public void testAncienneteSansDateEmbauche() {
		
		//Employé sans dateEmbauche
		
			//Given = Initialisation des données d'entrée
			Employe employe = new Employe();
			employe.setDateEmbauche(null);
			
			//When = Exécution de la méthode à tester
			Integer nbAnnees = employe.getNombreAnneeAnciennete();
			
			//Then = Vérifications de ce qu'a fait la méthode
			Assertions.assertThat(nbAnnees).isEqualTo(0);
			//AserJ pour faire des assertions bcp plus lisible que Junit
			
	}
	
	
	//Date dans le futur
	//on choisit de renvoyer undefined, null, ou ...
	//
	
	/* Dans la classe : employe.java
	public Integer getNombreAnneeAnciennete() {
        return LocalDate.now().getYear() - dateEmbauche.getYear();
    }
    private LocalDate dateEmbauche;
    */
	
	
	//5 paramètres qui varient
	//Matricule, performance, dateEmbauche, tempsPartiel, prime
		
		@ParameterizedTest
		@CsvSource({
			// Au moins 9 cas, scénarios à gérer, à tester
			//Matricule, performance, dateEmbauche, tempsPartiel, prime
			"1, 'T12345', 0, 1.0, 1000.0",
			"1, 'T12345', 0, 0.5, 500.0",
		    "1, 'M12345', 0, 1.0, 1700.0",
		    "2, 'T12345', 0, 1.0, 2300.0",
		    
		    //"'M12345', 2, 0, 0.5, 0",
		    
		    //"'M12345', 2, 2, 1.0, ",
		    //"'M12345', 2, 2, 0.5, ",
		    
		   //Run avec coverage

		        
		})
		public void testPrimeAnnuelle(Integer performance, String Matricule, Integer NombreAnneeAnciennete, Double tempsPartiel, Double prime) {
		    //Given, When, Then
			
			//Given, 
			Employe employe = new Employe();
			employe.setMatricule(Matricule);
			employe.setDateEmbauche(LocalDate.now().minusYears(NombreAnneeAnciennete));
			employe.setPerformance(performance);
			employe.setTempsPartiel(tempsPartiel);
			
			//When, 
			Double primeCalculee = employe.getPrimeAnnuelle();
			
			//Then
			Assertions.assertThat(primeCalculee).isEqualTo(prime);
			
			
			// NombreAnneeAnciennete avec :
			// Double primeAnciennete = Entreprise.PRIME_ANCIENNETE * this.getNombreAnneeAnciennete();
			// 2 cas possibles au moins NombreAnneeAnciennete >1 alors on a une primeAnciennete !=0
			//ou NombreAnneeAnciennete = 0 alors primeAnciennete = 0
			
		
		}
		
		
		
        //Calcule de la prime d'ancienneté
        //Double primeAnciennete = Entreprise.PRIME_ANCIENNETE * this.getNombreAnneeAnciennete();
        //Double prime;
        //Prime du manager (matricule commençant par M) : Prime annuelle de base multipliée par l'indice prime manager
        //plus la prime d'anciennté.
        //if(matricule != null && matricule.startsWith("M")) {
           // prime = Entreprise.primeAnnuelleBase() * Entreprise.INDICE_PRIME_MANAGER + primeAnciennete;
        //}
        //Pour les autres employés en performance de base, uniquement la prime annuelle plus la prime d'ancienneté.
        //else if (this.performance == null || Entreprise.PERFORMANCE_BASE.equals(this.performance)){
            //prime = Entreprise.primeAnnuelleBase() + primeAnciennete;
        //}
        //Pour les employés plus performance, on bonnifie la prime de base en multipliant par la performance de l'employé
        // et l'indice de prime de base.
        //else {
          //  prime = Entreprise.primeAnnuelleBase() * (this.performance + Entreprise.INDICE_PRIME_BASE) + primeAnciennete;
        //}
        //Au pro rata du temps partiel.
        //return prime * this.tempsPartiel;
		
		
	
	
}




