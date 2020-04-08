package com.ipiecoles.java.java350.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmployeTest {

    //Test unitaire qui test un seul cas
    @Test //Junit 5 import : avec jupiter soit Junit 5 : org.junit.jupiter.api.Test
    public void testAncienneteDateEmbaucheNmoins2() {
        //Employe dateEmbauche avec date 2ans avant aujourd'hui =>
        //2 annees d'anciennete
        //Given = Initialisation des donnees d'entree
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().minusYears(2));
        //When = Execution de la methode à tester
        Integer nbAnnees = employe.getNombreAnneeAnciennete();
        //Then = Verifications de ce qu'a fait la methode
        Assertions.assertThat(nbAnnees).isEqualTo(2);
        //AserJ pour faire des assertions bcp plus lisible que Junit
    }


    @Test
    public void testNbConges() {
        //Given = Initialisation des donnees d'entree
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().minusYears(2));
        //When = Execution de la methode à tester
        Integer nbConges = employe.getNbConges();
        //Then = Verifications de ce qu'a fait la methode
        Assertions.assertThat(nbConges).isEqualTo(27);
        //AserJ pour faire des assertions bcp plus lisible que Junit
    }


    @Test
    public void testAncienneteDateEmbaucheNplus2() {
        //Employe dateEmbauche avec date 2ans après aujourd'hui =>
        //0 annees d'anciennete
        //Given = Initialisation des donnees d'entree
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().plusYears(2));
        //When = Execution de la methode à tester
        Integer nbAnnees = employe.getNombreAnneeAnciennete();
        //Then = Verifications de ce qu'a fait la methode
        Assertions.assertThat(nbAnnees).isEqualTo(0);
        //AserJ pour faire des assertions bcp plus lisible que Junit
    }


    @Test
    public void testgetNbrRtt() {
        //Employe dateEmbauche avec date 2ans après aujourd'hui =>
        //0 annees d'anciennete
        //Given = Initialisation des donnees d'entree
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().plusYears(2));
        //When = Execution de la methode à tester
        Integer nbRtt = employe.getNbRtt();
        Integer nbRttActuel = employe.getNbRtt(LocalDate.now());
        Integer nbRttBixectile = employe.getNbRtt(LocalDate.of(2020, 1, 1));
        Integer nbRttNonBixectile = employe.getNbRtt(LocalDate.of(2019, 1, 1));
        //Then = Verifications de ce qu'a fait la methode
        Assertions.assertThat(nbRtt).isEqualTo(nbRttActuel);
        Assertions.assertThat(nbRttBixectile).isEqualTo(10);
        Assertions.assertThat(nbRttNonBixectile).isEqualTo(8);
        //AserJ pour faire des assertions bcp plus lisible que Junit
    }

    //Date aujourd'hui => 0
    @Test
    public void testAncienneteDateEmbaucheAujourdhui() {
        //Employe dateEmbauche avec date d'aujourd'hui =>
        //0 annees d'anciennete
        //Given = Initialisation des donnees d'entree
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now());
        //When = Execution de la methode à tester
        Integer nbAnnees = employe.getNombreAnneeAnciennete();
        Assertions.assertThat(nbAnnees).isEqualTo(0);
        //AserJ pour faire des assertions bcp plus lisible que Junit
    }

    //Date d'embauche indefinie => 0 (on considère que c 0 dans ce cas)
    @Test
    public void testAncienneteSansDateEmbauche() {
        //Employe sans dateEmbauche
        //Given = Initialisation des donnees d'entree
        Employe employe = new Employe();
        employe.setDateEmbauche(null);
        //When = Execution de la methode à tester
        Integer nbAnnees = employe.getNombreAnneeAnciennete();
        //Then = Verifications de ce qu'a fait la methode
        Assertions.assertThat(nbAnnees).isEqualTo(0);
        //AserJ pour faire des assertions bcp plus lisible que Junit
    }
    //5 paramètres qui varient
    //Matricule, performance, dateEmbauche, tempsPartiel, prime

    @ParameterizedTest
    @CsvSource({
            "1, 'T12345', 0, 1.0, 1000.0",
            "1, 'T12345', 0, 0.5, 500.0",
            "1, 'M12345', 0, 1.0, 1700.0",
            "2, 'T12345', 0, 1.0, 2300.0",
    })
    public void testPrimeAnnuelle(Integer performance, String Matricule, Integer NombreAnneeAnciennete, Double tempsPartiel, Double prime) {
        //Given,
        Employe employe = new Employe();    //Copie de la classe vide : objet vide
        //Propriete de l'objet Employe
        //setter pour mettre à jour les != proprietes de l'objet
        employe.setMatricule(Matricule);
        employe.setDateEmbauche(LocalDate.now().minusYears(NombreAnneeAnciennete));
        employe.setPerformance(performance);
        employe.setTempsPartiel(tempsPartiel);
        //When, Ce que je compare dans le test
        Double primeCalculee = employe.getPrimeAnnuelle();
        //Then, Faire la comparaison
        Assertions.assertThat(primeCalculee).isEqualTo(prime);
    }


    @Test
    public void lePourcentageNeDoitPasEtreNul() {
        Employe employe = new Employe();
        employe.setSalaire(2000.0);
        assertThrows(NullPointerException.class, () -> employe.augmenteSalaire(null));
    }


    @Test
    public void lePourcentageNeDoitPasEtreNegative() {
        Employe employe = new Employe();
        employe.setSalaire(2000.0);
        assertThrows(IllegalArgumentException.class, () -> employe.augmenteSalaire(-5.0));
    }


    @Test
    public void lePourcentagePositifEtNonNull() {
        Employe employe = new Employe();
        employe.setSalaire(2000.0);
        employe.augmenteSalaire(10.0);
        Assertions.assertThat(employe.getSalaire()).isEqualTo(2200);
    }
		
		/*La f° augmenteSalaire est mieux delimitee avec les tests TDD car on evite ainsi d'oublier des cas extrêmes possibles 
		en ecrivant ces tests et en adaptant la fonction augmenteSalaire à chacun des tests. */
    //2021 : l'annee est non bissextile, a debute un vendredi et il y a 7 jours feries ne tombant pas le week-end.
    //2022 : l'annee est non bissextile, a debute un samedi et il y a 7 jours feries ne tombant pas le week-end.
    //2032 : l'annee est bissextile, a debute un jeudi et il y a 7 jours feries ne tombant pas le week-end.
    //2019 : l'annee est non bissextile, a debute un mardi et il y a 10 jours feries ne tombant pas le week-end.
    @Test
    public void nombreDeJourFerriesSansWeekEndEtNonBissextileDebutantUnMardi() {
        Employe employe = new Employe();
        int nombreJourFeriesSansWeekEnd = employe.getNombreJourFerierSansWeekend(LocalDate.of(2019, 1, 1));
        Assertions.assertThat(nombreJourFeriesSansWeekEnd).isEqualTo(10);
    }


    //1980 : l'annee est bissextile, a debute un mardi et il y a 10 jours feries ne tombant pas le week-end.
    @Test
    public void nombreJourFerriesSansWeeEndEstBissextileDebutantUnMardi() {
        Employe employe = new Employe();
        employe.setId(500l);
        employe.setNom("Dao");
        employe.setPrenom("gab");
        int nombreJourFeriesSansWeekEnd = employe.getNombreJourFerierSansWeekend(LocalDate.of(2036, 1, 1));
        Assertions.assertThat(nombreJourFeriesSansWeekEnd).isEqualTo(10);
    }

    //2021 : l'annee est non bissextile, a debute un vendredi et il y a 7 jours feries ne tombant pas le week-end.
    @Test
    public void nombreJourFerriesSansWeekEndEstNonBissextilDebutantUnVendredi() {
        Employe employe = new Employe();
        int nombreJourFeriesSansWeekEnd = employe.getNombreJourFerierSansWeekend(LocalDate.of(2021, 1, 1));
        Assertions.assertThat(nombreJourFeriesSansWeekEnd).isEqualTo(7);
    }


    //2022 : l'annee est non bissextile, a debute un samedi et il y a 7 jours feries ne tombant pas le week-end.
    @Test
    public void nombreJourFerriesSansWeekEndEstNonBissextileDebutantUnSamedi() {
        Employe employe = new Employe();
        int nombreJourFeriesSansWeekEnd = employe.getNombreJourFerierSansWeekend(LocalDate.of(2022, 1, 1));
        Assertions.assertThat(nombreJourFeriesSansWeekEnd).isEqualTo(7);
    }


    //2032 : l'annee est bissextile, a debute un jeudi et il y a 7 jours feries ne tombant pas le week-end.
    @Test
    public void nombreJourFerriesSansWeekEndEstBissextileDebutantUnJeudi() {
        Employe employe = new Employe();
        int nombreJourFeriesSansWeekEnd = employe.getNombreJourFerierSansWeekend(LocalDate.of(2032, 1, 1));
        Assertions.assertThat(nombreJourFeriesSansWeekEnd).isEqualTo(7);
    }

    //2020 : tester le nombre samedi dimanche
    @Test
    public void leNombreSamediEtDimancheEn2020() {
        Employe employe = new Employe();
        int nombreJourSamediDimanche = employe.getNombreSamediDimanche(LocalDate.of(2020, 1, 1));
        Assertions.assertThat(nombreJourSamediDimanche).isEqualTo(104 );
    }
    //2036 : tester le nombre samedi dimanche

    @Test
    public void leNombreDeSamediEtDimancheEn2036() {
        Employe employe = new Employe();
        int nombreJourSamediDimanche = employe.getNombreSamediDimanche(LocalDate.of(2036, 1, 1));
        Assertions.assertThat(nombreJourSamediDimanche).isEqualTo(104);
    }

    @Test
    public void leNombreDeSamediEtDimancheEn2030() {
        Employe employe = new Employe();
        int nombreJourSamediDimanche = employe.getNombreSamediDimanche(LocalDate.of(2030, 1, 1));
        Assertions.assertThat(nombreJourSamediDimanche).isEqualTo(104);
    }
    //2021 : tester le nombre samedi dimanche

    @Test
    public void leNombreDeSamediEtDimancheEn2021() {
        Employe employe = new Employe();
        int nombreJourSamediDimanche = employe.getNombreSamediDimanche(LocalDate.of(2021, 1, 1));
        Assertions.assertThat(nombreJourSamediDimanche).isEqualTo(104);
    }
    //2019 : tester le nombre samedi dimanche

    @Test
    public void leNombreDeSamediEtDimancheEn2019() {
        Employe employe = new Employe();
        int nombreJourSamediDimanche = employe.getNombreSamediDimanche(LocalDate.of(2019, 1, 1));
        Assertions.assertThat(nombreJourSamediDimanche).isEqualTo(104);
    }


    //2018 : tester le nombre samedi dimanche
    @Test
    public void leNombreDeSamediEtDimancheEn2018() {
        Employe employe = new Employe();
        int nombreJourSamediDimanche = employe.getNombreSamediDimanche(LocalDate.of(2018, 1, 1));
        Assertions.assertThat(nombreJourSamediDimanche).isEqualTo(104);
    }


    //2025 : tester le nombre samedi dimanche
    @Test
    public void leNombreDeSamediEtDimancheEn2025() {
        Employe employe = new Employe();
        int nombreJourSamediDimanche = employe.getNombreSamediDimanche(LocalDate.of(2025, 1, 1));
        Assertions.assertThat(nombreJourSamediDimanche).isEqualTo(104);
    }


    //2044 : tester le nombre samedi dimanche
    @Test
    public void leNombreDeSamediEtDimancheEn2044() {
        Employe employe = new Employe();
        int nombreJourSamediDimanche = employe.getNombreSamediDimanche(LocalDate.of(2044, 1, 1));
        Assertions.assertThat(nombreJourSamediDimanche).isEqualTo(105);
    }


    //2022 : tester le nombre samedi dimanche
    @Test
    public void leNombreDeSamediEtDimancheEn2022() {
        Employe employe = new Employe();
        int nombreJourSamediDimanche = employe.getNombreSamediDimanche(LocalDate.of(2022, 1, 1));
        Assertions.assertThat(nombreJourSamediDimanche).isEqualTo(105);
    }


    @Test
    public void testEqualsAndHash() {
        Employe employe1 = new Employe("Doe", "John", "M12345", LocalDate.now(), 1500d, 1, 1.0);
        Employe employe2 = new Employe("Doe", "John", "M12345", LocalDate.now(), 1500d, 1, 1.0);
        Assertions.assertThat(employe1.equals(employe2)).isEqualTo(true);
        Assertions.assertThat(employe1.equals(employe1)).isEqualTo(true);
        Assertions.assertThat(employe1.equals(new Object())).isEqualTo(false);
        Assertions.assertThat(employe1.hashCode()).isEqualTo(employe2.hashCode());
    }
}
