package entity;

public enum Departement {


    CHIMIE("C", "Chimie", "Chimie"),
    SCI_BIO("SB", "Sciences biologiques", "Sciences biologiques"),
    GEOGRAPHIE("G", "Géographie", "Géographie"),
    PHYSIQUE("P", "Physique", "Physique"),
    NONE("\0", "NONE", "NONE");

    public final String pathId, directory, displayName;

    Departement(String pathId, String directory, String displayName) {
        this.pathId=pathId; this.directory=directory; this.displayName=displayName;
    }

    public static Departement of(String name){
        switch (name.toLowerCase()){
            case "chimie": return CHIMIE;
            case "sciences biologiques": return SCI_BIO;
            case "géographie": return GEOGRAPHIE;
            case "physique": return PHYSIQUE;
            default: return NONE;
        }
    }


}
