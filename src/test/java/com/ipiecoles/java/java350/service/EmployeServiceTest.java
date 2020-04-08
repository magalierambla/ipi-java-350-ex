package com.ipiecoles.java.java350.service;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import com.ipiecoles.java.java350.service.EmployeService;


/*
 * Méthode dans laquelle on simule l'utilisation de dépendances extérieures via des mocks (ici dépendance = repo).
 * Permet de rester dans un test unitaire (si échec, c'est forcément dû au contenu de la méthode)
 * simuler les dépendances permet de tester les méthodes d'un service par ex avant qu'une méthode ait été codée dans le repo.
 * Il faudra ensuite un test d'intégration (sur l'assemblage des modules, ici le repoet le service)
 * Pas vesoin de @Autowired dans le cas d'un mock : on utilise plutôt @InjectMocks + @Mock pour chaque dépendance
 */


//Test plus rapide, pertinent, indépendant => principe du Mock
@ExtendWith(MockitoExtension.class) 
public class EmployeServiceTest {
	
	@InjectMocks
	private EmployeService employeService;
	
	@Mock
	private EmployeRepository employeRepository;
	
	@Test
	public void testEmbaucheEmployeCommercialPleinTempsBTS() throws EmployeException {
		
	    //Given
		String nom= "Doe";
		String prenom= "John";
		Poste poste = Poste.COMMERCIAL;
		NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
		Double tempsPartiel = 1.0;
		
		//On simule une base contenant déjà des employés
		//findLastMatricule => 00345 / null (2 scénarios possibles)
		Mockito.when(employeRepository.findLastMatricule()).thenReturn("00345");
		
		//A partir de ce dernier matricule artificiel, on incrémente pour créer un nouveau commercial
		//findByMatricule => null
		Mockito.when(employeRepository.findByMatricule("C00346")).thenReturn(null);
		
	    //When Junit 5
	    employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
	    //Comme ce test n'est pas supposé lever l'exception, on ajoute un throw au niveau de la signature (et non un try/catch)
	    //Assertions.assertThrows(EntityNotFoundException.class, () -> vehiculeService.findByImmat("X"));
	    
	    //Then
	    //On utilise un capteur d'arguments pour récupérer les valeurs des arguments au moment du save
	    //BDD si l'employé est bien créé (nom, prenom, matricule, salaire, dateEmbauche, performavce, temps Partiel)
	    //Employe employe = new Employe();
	    
	    //Initialisation du capteur d'argument
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        //pour verifier qu'une méthode est bien appelé avec Mockito.verify
        //La méthode save est appelé une fois et est capturé
        Mockito.verify(employeRepository, Mockito.times(1)).save(employeArgumentCaptor.capture());
        //ou équivalent à Mockito.verify(employeRepository.save(employeCaptor.capture());
        // on p enlever Mockito.times(1)
        Employe employe = employeArgumentCaptor.getValue();
        // On vérifie la valeur de tt les employes
        //Autre possibilité : 
        //Employe employeVerif = new Employe(nom, prenom,...);
        //Assertions.assertThat(employe).isEqualTo(employeVerif);
        //On récupère la valeur qui est passé en paramètre du save dans mon repository
        //Assertions.assertThat(vehiculeCaptor.getValue().getProprietaireId()).isEqualTo(2L);
        Assertions.assertThat(employe.getNom()).isEqualTo(nom);
        Assertions.assertThat(employe.getPrenom()).isEqualTo(prenom);
        Assertions.assertThat(employe.getMatricule()).isEqualTo("C00346");
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        //Assertions.assertThat(employe.getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd"))).isEqualTo(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(tempsPartiel);
        //Assertions.assertThat(employe.getPerformance()).isEqualTo(tempsPartiel);
        Assertions.assertThat(employe.getPerformance()).isEqualTo(Entreprise.PERFORMANCE_BASE);
        //Assertions.assertThat(employe.getPerformance()).isEqualTo(1);
        
        //1521.22 * 1.2 * 1.0 
        //Pour le salaire, on fait le calcul salaire_base * coef selon nivEtude plutôt que d'utiliser la formule dans le code
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1825.46);	
        
        //Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        //Assertions.assertThat(employe.getDateEmbauche()).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        
        //Assertions.assertThat(employe.getPerformance()).isEqualTo(tempsPartiel);
        
        //Autre option
        //Employe employeVerif = new Employe(nom, prenom,...);
        //Assertions.assertThat(employe).isEqualTo(employeVerif);
        
	}
	
	
	@Test
	public void testEmbaucheEmployeLimiteMatricule() throws EmployeException {
		
	    //Given
		String nom= "Doe";
		String prenom= "John";
		Poste poste = Poste.COMMERCIAL;
		NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
		Double tempsPartiel = 1.0;
		//On simule une base contenant déjà des employés
		Mockito.when(employeRepository.findLastMatricule()).thenReturn("99999");
		
		//soit ça 
		//When/Then AssertJ 
		//Option pour remplacer le try/catch avec les Lambdas :
		/*
		Assertions.assertThatThrownBy(
				() -> {
					employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
				})
		.isInstanceOf(EmployeException.class).hasMessage("Limites des 100000 matricules atteinte !");	
	   */
		//ou ça 
		//When Junit 5
		try {
			employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
			Assertions.fail("Aurait du planter !");
		} catch (Exception e) {
			//Then
			Assertions.assertThat(e).isInstanceOf(EmployeException.class);
		}
		
	  
	}
}
	





//      Employe employe = new Employe(nom, prenom, matricule, LocalDate.now(), salaire, Entreprise.PERFORMANCE_BASE, tempsPartiel);


//  String lastMatricule = employeRepository.findLastMatricule();
//if(lastMatricule == null){
 //   lastMatricule = Entreprise.MATRICULE_INITIAL;
//}
//... et incrémentation
//Integer numeroMatricule = Integer.parseInt(lastMatricule) + 1;
//if(numeroMatricule >= 100000){
  //  throw new EmployeException("Limite des 100000 matricules atteinte !");
//}