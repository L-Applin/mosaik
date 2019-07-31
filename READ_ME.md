# Options ligne de commande

Note : Afin d'exécuter le fichier `.jar` correctement, veuillez suivre les étapes suivantes : 

1. Ouvrir le terminal de votre choix.
2. Naviguer jusqu'au fichier `.jar`. <b>IMPORTANT</b> : le fichier jar doit être exécuter à partir de l'emplacement 
courant de l'invite de commande.
3. utiliser la commande suivante pour lancer l'application : `java -jar <nom du ficheir>.jar [options]` où les options 
sont facultatives parmis les suivantes :  

###`--lang=fr|en`
Permet de changer la langue d'affichage du logiciel. Par défaut : `--lang=fr`. Présentement seulemetn le français et l'anglais 
sont supportés.

###`--img=<PATH>`
Permet de spécifier l'endroit où ce trouve les images des mosaïques. 
Si omis, le logiciel utilise par défaut utilise l'emplacement : `data/img/<fichier_image>` qui se trouve à partir de 
l'emplacement actuel du fichier `.jar`. Ce fichier doit contenir des sous fichiers où les mosaiques de chaque 
départements sont subdivisées. Les noms doivent EXACTEMENT correspondre au schéma suivant :  


###`--update=<PATH>`
Permet de mettre à jour les informations des étudiants gradués. Les anciennes informations seront <b>REMPLACÉES</b> 
par le contenu du nouveau fichier. Différents types de fichier sont supportés. Le plus simple et sécuraitaire est 
d'utiliser un ficher .xls dont le format est précisée plus bas. Supporte aussi des fichiers .csv ayant 
un format adéquat.

### `--run=f|t|false|true`
Permet ne pas lancer l'application si `f` ou `false` est spécifié. Permet entre autres de faire des mise à jour 
sans lancer l'application.

exemple d'utilisation : 

`java -jar mosaik-1.0-SNAPSHOT-jar-with-dependencies.jar --img=E:\images --lang=en`

Lancerait l'application en anglais avec comme référence d'image un dossier 'images' qui se trouve sur le disque E.

`java -jar mosaik-1.0-SNAPSHOT-jar-with-dependencies.jar --run=f --update=D:\ListeNomsPourMosaïques_20190808.xls`

Permetrait de mettre à jour les informations concernant les étudiants et leur mosaique associé sans lancer 
l'application.

par défaut : `java -jar <jar_file_name>.jar` lancera l'application en français <b>sans</b> procéder à 
la mise à jour des informations et en prenant en considération qu'il existe un dossier 
`data/img/mosaique` dont le dossier data qui se situe au même niveau que le fichier `.jar`.

# format des fichiers "data"
L'application prend pour acquis un certain format des données. Afin de pouvoir lancer l'application sans problème, ces
formats doivent être respectés. Toute donnée ne respectant pas ce format risque de causer desproblèmes au lancement. Un
effort a été mis pour tenter d'expliquer la cause d'un problème, le cas échéant.

