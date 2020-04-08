package com.ipiecoles.java.java350.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;

import static java.time.DayOfWeek.SATURDAY;

@Entity
public class Employe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nom;

    private String prenom;

    private String matricule;

    private LocalDate dateEmbauche;

    private Double salaire = Entreprise.SALAIRE_BASE;

    private Integer performance = Entreprise.PERFORMANCE_BASE;

    private Double tempsPartiel = 1.0;

    public Employe() {
    }

    public Employe(String nom, String prenom, String matricule, LocalDate dateEmbauche, Double salaire,
                   Integer performance, Double tempsPartiel) {
        this.nom = nom;
        this.prenom = prenom;
        this.matricule = matricule;
        this.dateEmbauche = dateEmbauche;
        this.salaire = salaire;
        this.performance = performance;
        this.tempsPartiel = tempsPartiel;
    }

    public Integer getNombreAnneeAnciennete() {
        if (dateEmbauche != null && dateEmbauche.isBefore(LocalDate.now())) {
            return LocalDate.now().getYear() - dateEmbauche.getYear();
        }
        return 0;
    }
    // On déf les 4 test unitaires puis on code

    public Integer getNbConges() {
        return Entreprise.NB_CONGES_BASE + this.getNombreAnneeAnciennete();
    }

    public Integer getNbRtt() {
        return getNbRtt(LocalDate.now());
    }

    // Nombre de jours dans l'année (i1 => nombreJourAnnee)
    // - Nombre de jours travaillés dans l'année en plein temps
    // Entreprise.NB_JOURS_MAX_FORFAIT
    // -Nombre de samedi et dimanche dans l'année (var => nombreSamediEtDimanche)
    // - Nombre de jours fériés ne tombant pas le week-end (monInt =>
    // nombreJourFeriesSansWeekEnd
    // - Nombre de congés payés Entreprise.NB_CONGES_BASE
    public Integer getNbRtt(LocalDate d) {
        int nombreJourAnnee = d.isLeapYear() ? 366 : 365;
        int nombreSamediEtDimanche = getNombreSamediDimanche(d);
        int nombreJourFeriesSansWeekEnd = getNombreJourFerierSansWeekend(d);
        // https://www.tutorialspoint.com/what-is-the-difference-between-math-ceil-and-math-round-methods-in-javascript
        return (int) Math.ceil((nombreJourAnnee - Entreprise.NB_JOURS_MAX_FORFAIT - nombreSamediEtDimanche
                - Entreprise.NB_CONGES_BASE - nombreJourFeriesSansWeekEnd) * tempsPartiel);
    }


	/*


  Il y a 52 semaines, donc 104 samedis et dimanches par an au minimum.

 l'année non bissextile commence et finit toujours par le même jour. Par exemple si le 01/01 est un mardi, le 31/12 est un mardi aussi.
Ce qui implique que si l'année commence un samedi, elle finit aussi un samedi. On a donc cette année-là un jour de week-end en plus. Idem si l'année commence un dimanche.

Enfin, si l'année est bissextile, on aura un décalage d'un jour entre le premier jour dans l"année et le dernier jour de l'année. Ce qui implique que :

si l'année commence un Vendredi, elle finit un Samedi. Donc +1.
si l'année commence un Samedi, elle finit un Dimanche. Donc +2.
si l'année commence un Dimanche, elle finit un Lundi. Donc +1.

     */
	public int getNombreSamediDimanche(LocalDate d) {
		int nombreSamediEtDimanche = 104;

		DayOfWeek premierJourDannee = LocalDate.of(d.getYear(), 1, 1).getDayOfWeek();
		if (!d.isLeapYear()){

			if(premierJourDannee == SATURDAY ||  premierJourDannee == SATURDAY  ){
				nombreSamediEtDimanche++;
			}

		}else{
			DayOfWeek dernierJourDannee = LocalDate.of(d.getYear(), 12, 31).getDayOfWeek();


			if(premierJourDannee == SATURDAY ||  premierJourDannee == SATURDAY  ){
				nombreSamediEtDimanche++;
			}
			if(dernierJourDannee == SATURDAY ||  dernierJourDannee == SATURDAY  ){
				nombreSamediEtDimanche++;
			}

		}



		return nombreSamediEtDimanche;
	}

    public int getNombreJourFerierSansWeekend(LocalDate d) {
        return (int) Entreprise.joursFeries(d).stream()
                .filter(localDate -> localDate.getDayOfWeek().getValue() <= DayOfWeek.FRIDAY.getValue()).count();
    }

    /**
     * Calcul de la prime annuelle selon la règle : Pour les managers : Prime
     * annuelle de base bonnifiée par l'indice prime manager Pour les autres
     * employés, la prime de base plus éventuellement la prime de performance
     * calculée si l'employé n'a pas la performance de base, en multipliant la prime
     * de base par un l'indice de performance (égal à la performance à laquelle on
     * ajoute l'indice de prime de base)
     * <p>
     * Pour tous les employés, une prime supplémentaire d'ancienneté est ajoutée en
     * multipliant le nombre d'année d'ancienneté avec la prime d'ancienneté. La
     * prime est calculée au pro rata du temps de travail de l'employé
     *
     * @return la prime annuelle de l'employé en Euros et cents
     */
    public Double getPrimeAnnuelle() {
        // Calcule de la prime d'ancienneté
        Double primeAnciennete = Entreprise.PRIME_ANCIENNETE * this.getNombreAnneeAnciennete();
        Double prime;
        // Prime du manager (matricule commençant par M) : Prime annuelle de base
        // multipliée par l'indice prime manager
        // plus la prime d'anciennté.
        if (matricule != null && matricule.startsWith("M")) {
            prime = Entreprise.primeAnnuelleBase() * Entreprise.INDICE_PRIME_MANAGER + primeAnciennete;
        }
        // Pour les autres employés en performance de base, uniquement la prime annuelle
        // plus la prime d'ancienneté.
        else if (this.performance == null || Entreprise.PERFORMANCE_BASE.equals(this.performance)) {
            prime = Entreprise.primeAnnuelleBase() + primeAnciennete;
        }
        // Pour les employés plus performance, on bonnifie la prime de base en
        // multipliant par la performance de l'employé
        // et l'indice de prime de base.
        else {
            prime = Entreprise.primeAnnuelleBase() * (this.performance + Entreprise.INDICE_PRIME_BASE)
                    + primeAnciennete;
        }
        // Au pro rata du temps partiel.
        return prime * this.tempsPartiel;
    }
    // Augmenter salaire
    // public void augmenterSalaire(double pourcentage){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * @param nom the nom to set
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @return the prenom
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * @param prenom the prenom to set
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * @return the matricule
     */
    public String getMatricule() {
        return matricule;
    }

    /**
     * @param matricule the matricule to set
     */
    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    /**
     * @return the dateEmbauche
     */
    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    /**
     * @param dateEmbauche the dateEmbauche to set
     */
    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    /**
     * @return the salaire
     */
    public Double getSalaire() {
        return salaire;
    }

    /**
     * @param salaire the salaire to set
     */
    public void setSalaire(Double salaire) {
        this.salaire = salaire;
    }

    public Integer getPerformance() {
        return performance;
    }

    public void setPerformance(Integer performance) {
        this.performance = performance;
    }

    public Double getTempsPartiel() {
        return tempsPartiel;
    }

    public void setTempsPartiel(Double tempsPartiel) {
        this.tempsPartiel = tempsPartiel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Employe))
            return false;
        Employe employe = (Employe) o;
        return Objects.equals(id, employe.id) && Objects.equals(nom, employe.nom)
                && Objects.equals(prenom, employe.prenom) && Objects.equals(matricule, employe.matricule)
                && Objects.equals(dateEmbauche, employe.dateEmbauche) && Objects.equals(salaire, employe.salaire)
                && Objects.equals(performance, employe.performance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, prenom, matricule, dateEmbauche, salaire, performance);
    }

    public void augmenteSalaire(Double pourcentage) {
        if (pourcentage == null)
            throw new NullPointerException();
        if (pourcentage < 0)
            throw new IllegalArgumentException();
        salaire = salaire + (salaire * (pourcentage / 100));
    }
}
