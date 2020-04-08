package com.ipiecoles.java.java350.repository;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
//Test avec Spring sur fichier EmployeRepository.java dans src/main/java dans repository

	/* Rappel du fichier EmployeRepository.java
	 @Repository
	public interface EmployeRepository extends JpaRepository<Employe, Long> {
	    @Query("select max(substring(matricule,2)) from Employe")
	    String findLastMatricule();
	
	    Employe findByMatricule(String matricule);
	
	    @Query("select avg(performance) from Employe where SUBSTRING(matricule,0,1) = ?1 ")
	    Double avgPerformanceWhereMatriculeStartsWith(String premiereLettreMatricule);
	}
	*/








import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ipiecoles.java.java350.model.Employe;
//@ExtendWith() //Junit 5
//@DataJpaTest // ou @SpringBootTest

@SpringBootTest
public class EmployeRepositoryTest {

    @Autowired
    EmployeRepository employeRepository;

    //Méthode pour nettoyer (éviter le pb d'étancheité entre les 2 tests suivant)
    @BeforeEach
    public void setup() {
        employeRepository.deleteAll();
    }

    @Test
    public void testFindLastMatriculeZ() {

        //When
        String lastMatricule = employeRepository.findLastMatricule();
        //Then
        Assertions.assertThat(lastMatricule).isNull();    //test aux limites 
    }

    @Test
    public void testFindLastMatricule2Employes() {
        //Given
        Employe employe1 = employeRepository.save(new Employe("Doe", "John", "M12345", LocalDate.now(), 1500d, 1, 1.0));
        Employe employe2 = employeRepository.save(new Employe("Doe", "Jane", "T01234", LocalDate.now(), 1500d, 1, 1.0));
        //When
        String lastMatricule = employeRepository.findLastMatricule();
        //Then
        Assertions.assertThat(lastMatricule).isEqualTo("12345");
    }
    

    @Test
    public void testAvgPerformanceWhereMatriculeStartsWith() {
        //Given
        Employe employe1 = employeRepository.save(new Employe("Doe", "John", "M12345", LocalDate.now(), 1500d, 1, 1.0));
        Employe employe2 = employeRepository.save(new Employe("Doe", "Jane", "T01234", LocalDate.now(), 1500d, 1, 1.0));
        Employe employe3 = employeRepository.save(new Employe("Dark", "Blaise", "C01234", LocalDate.now(), 1500d, 6, 1.0));
        Employe employe4 = employeRepository.save(new Employe("Marcy", "Laura", "M01234", LocalDate.now(), 1500d, 7, 1.0));
        Employe employe5 = employeRepository.save(new Employe("TOUR", "Lise", "M0234", LocalDate.now(), 1500d, 10, 1.0));
        //When
        Double moyenneM = employeRepository.avgPerformanceWhereMatriculeStartsWith("M");
        Double moyenneT = employeRepository.avgPerformanceWhereMatriculeStartsWith("T");
        Double moyenneC = employeRepository.avgPerformanceWhereMatriculeStartsWith("C");
        Double moyenneX = employeRepository.avgPerformanceWhereMatriculeStartsWith("X");
        //Then
        Assertions.assertThat(moyenneM).isEqualTo(6);
        Assertions.assertThat(moyenneC).isEqualTo(6);
        Assertions.assertThat(moyenneT).isEqualTo(1);
        Assertions.assertThat(moyenneX).isEqualTo(null);
    }
}