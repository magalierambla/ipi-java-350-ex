package com.ipiecoles.java.java350.service;

import static org.junit.jupiter.api.Assertions.assertThrows;


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
        // Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.COMMERCIAL;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("00345");
        Mockito.when(employeRepository.findByMatricule("C00346")).thenReturn(null);
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository, Mockito.times(1)).save(employeArgumentCaptor.capture());
        Employe employe = employeArgumentCaptor.getValue();
        Assertions.assertThat(employe.getNom()).isEqualTo(nom);
        Assertions.assertThat(employe.getPrenom()).isEqualTo(prenom);
        Assertions.assertThat(employe.getMatricule()).isEqualTo("C00346");
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(tempsPartiel);
        Assertions.assertThat(employe.getPerformance()).isEqualTo(Entreprise.PERFORMANCE_BASE);
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1825.46);
    }

    @Test
    public void testEmbaucheEmployeLimiteMatricule() throws EmployeException {
        // Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.COMMERCIAL;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        // On simule une base contenant déjà des employés
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("99999");
        try {
            employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
            Assertions.fail("Aurait du planter !");
        } catch (Exception e) {
            // Then
            Assertions.assertThat(e).isInstanceOf(EmployeException.class);
        }
    }

    @Test
    public void testCaTraiteNonNullEtPositif() throws EmployeException {
        assertThrows(EmployeException.class, () -> employeService.calculPerformanceCommercial("abcd", null, 100l));
    }

    @Test
    public void testCaTraitePositif() throws EmployeException {
        assertThrows(EmployeException.class, () -> employeService.calculPerformanceCommercial("abcd", -100l, 100l));
    }
    //tester que CaObjectif nest pas null et n'est negative

    @Test
    public void testCaObjectifNonNullEtPositif() throws EmployeException {
        assertThrows(EmployeException.class, () -> employeService.calculPerformanceCommercial("abcd", 300l, null));
    }


    @Test
    public void testCaObjectifPositif() throws EmployeException {
        assertThrows(EmployeException.class, () -> employeService.calculPerformanceCommercial("abcd", 100l, -100l));
    }


    //tester que matricule soit non null et première lettre matricule soit C
    @Test
    public void testMatriculeDoitCommencerParC() throws EmployeException {
        assertThrows(EmployeException.class, () -> employeService.calculPerformanceCommercial("M1200", 100l, 100l));
    }

    @Test
    public void testMatriculeDoitEtreNonNull() throws EmployeException {
        assertThrows(EmployeException.class, () -> employeService.calculPerformanceCommercial(null, 100l, 100l));
    }


    @Test
    public void testEmployerExiste() throws EmployeException {
        Mockito.when(employeRepository.findByMatricule("C00346")).thenReturn(null);
        assertThrows(EmployeException.class, () -> employeService.calculPerformanceCommercial("C00346", 100l, 100l));
    }


    /*
     * 1 : Si le chiffre d'affaire est inférieur de plus de 20% à l'objectif fixé, le commercial retombe à la performance de base
     * 2 : Si le chiffre d'affaire est inférieur entre 20% et 5% par rapport à l'ojectif fixé, il perd 2 de performance (dans la limite de la performance de base)
     * 3 : Si le chiffre d'affaire est entre -5% et +5% de l'objectif fixé, la performance reste la même.
     * 4 : Si le chiffre d'affaire est supérieur entre 5 et 20%, il gagne 1 de performance
     * 5 : Si le chiffre d'affaire est supérieur de plus de 20%, il gagne 4 de performance
     *
     * 6 : Si la performance ainsi calculée est supérieure à la moyenne des performances des commerciaux, il reçoit + 1 de performance.
     */


    // * 1 : Si le chiffre d'affaire est inférieur de plus de 20% à l'objectif fixé, le commercial retombe à la performance de base
    @Test
    public void testPerformanceCas1() throws EmployeException {
        Employe employe = new Employe();
        employe.setMatricule("C00346");
        employe.setPerformance(2);
        Mockito.when(employeRepository.findByMatricule("C00346")).thenReturn(employe);
        Mockito.when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(3d);
        employeService.calculPerformanceCommercial("C00346", 15l, 100l);
        Assertions.assertThat(employe.getPerformance()).isEqualTo(1);
    }

    // * 2 : Si le chiffre d'affaire est inférieur entre 20% et 5% par rapport à l'ojectif fixé, il perd 2 de performance (dans la limite de la performance de base)
    @Test
    public void testPerformanceCas2() throws EmployeException {
        Employe employe = new Employe();
        employe.setMatricule("C00346");
        employe.setPerformance(2);
        Mockito.when(employeRepository.findByMatricule("C00346")).thenReturn(employe);
        Mockito.when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(3d);
        employeService.calculPerformanceCommercial("C00346", 15l, 100l);
        Assertions.assertThat(employe.getPerformance()).isEqualTo(1);
    }
    // ca objetc 100
    // -5 % 100 =>95

    // +5% de 100% ==> 105
    //  * 3 : Si le chiffre d'affaire est entre -5% et +5% de l'objectif fixé, la performance reste la même.
    @Test
    public void testPerformanceCas3() throws EmployeException {
        Employe employe = new Employe();
        employe.setMatricule("C00346");
        employe.setPerformance(2);
        Mockito.when(employeRepository.findByMatricule("C00346")).thenReturn(employe);
        Mockito.when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(3d);
        employeService.calculPerformanceCommercial("C00346", 98l, 100l);
        Assertions.assertThat(employe.getPerformance()).isEqualTo(2);
    }
    // ca 100
    // +5 ==> 105
    // +20 ==> 120

    // * 4 : Si le chiffre d'affaire est supérieur entre 5 et 20%, il gagne 1 de performance
    @Test
    public void testPerformanceCas4() throws EmployeException {
        Employe employe = new Employe();
        employe.setMatricule("C00346");
        employe.setPerformance(2);
        Mockito.when(employeRepository.findByMatricule("C00346")).thenReturn(employe);
        Mockito.when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(3d);
        employeService.calculPerformanceCommercial("C00346", 115l, 100l);
        Assertions.assertThat(employe.getPerformance()).isEqualTo(3);
    }


    // * 5 : Si le chiffre d'affaire est supérieur de plus de 20%, il gagne 4 de performance
    @Test
    public void testPerformanceCas5() throws EmployeException {
        Employe employe = new Employe();
        employe.setMatricule("C00346");
        employe.setPerformance(2);
        Mockito.when(employeRepository.findByMatricule("C00346")).thenReturn(employe);
        Mockito.when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(7d);
        employeService.calculPerformanceCommercial("C00346", 128l, 100l);
        Assertions.assertThat(employe.getPerformance()).isEqualTo(6);
    }

    //* 6 : Si la performance ainsi calculée est supérieure à la moyenne des performances des commerciaux, il reçoit + 1 de performance.
    @Test
    public void testPerformanceCas6() throws EmployeException {
        Employe employe = new Employe();
        employe.setMatricule("C00346");
        employe.setPerformance(2);
        Mockito.when(employeRepository.findByMatricule("C00346")).thenReturn(employe);
        Mockito.when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(2d);
        employeService.calculPerformanceCommercial("C00346", 128l, 100l);
        Assertions.assertThat(employe.getPerformance()).isEqualTo(7);
    }
}
