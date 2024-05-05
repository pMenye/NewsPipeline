# tp_bigdata_etl


----------------------------------- DESCRIPTION GLOBALE DE LA SOLUTION -------------------------------------------------------

Notre solution vise à automatiser le processus de récupération, de traitement et de stockage de données provenant de l'API News API, en utilisant une architecture basée sur des microservices et des conteneurs Docker, orchestrée par Docker-compose.

Tout d'abord, nous avons mis en place un composant appelé "Aggregator" qui interagit avec l'API News API pour récupérer des données en masse. Cette composante est responsable de la collecte initiale des données brutes.
Ce microservice reçoit les données de l'API News API et les formate selon un modèle prédéfini. Une fois les données formatées, l'Aggregator les publie sur un topic Kafka approprié. Cette étape de publication sur Kafka permet une séparation claire des différentes catégories de données en fonction de leurs sujets.

Dans cette configuration, Kafka agit comme un système de messagerie et de traitement des flux de données en temps réel. Il permet de décharger les producteurs de données (comme l'Aggregator) des consommateurs (comme le Topic-subcription) en agissant comme une file d'attente des données, assurant ainsi une séparation des responsabilités et une scalabilité efficace. En publiant les données sur des topics spécifiques, Kafka facilite le traitement parallèle des flux de données et garantit la livraison des messages, contribuant ainsi à la fiabilité et à la performance globale du système.

Puis, nous avons mis en place un service appelé "Topic Consumer". Ce service consomme les données à partir des topics Kafka et les enregistre dans une base de données MongoDB. Le Topic Consumer assure la persistance des données dans un format structuré et adapté à une analyse ultérieure.

Tous ces composants, y compris l'Aggregator, le Topic-subscription, ainsi que Kafka et MongoDB, sont conteneurisés à l'aide de Docker. Cette approche de conteneurisation offre une portabilité, une isolation et une scalabilité accrues pour notre solution.

Enfin, nous utilisons Docker-compose pour orchestrer l'ensemble du système, permettant ainsi un déploiement simple et cohérent de tous les composants de notre solution.

En résumé, notre solution réalise un ETL complet en automatisant complètement le processus de récupération, de traitement et de stockage de données provenant de l'API News API, en utilisant une architecture basée sur des microservices et des conteneurs Docker, orchestrée par Docker-compose.



-----------------------------------------------GUIDE D'INSTALLATION DE LA SOLUTION------------------------------------------------------------------


1. Installer Docker et Docker-compose : Assurez-vous que Docker et Docker-compose sont installés sur votre machine. Vous pouvez les télécharger et les installer depuis les sites officiels de Docker.

2. Cloner le dépôt Git : Clonez le dépôt Git contenant les fichiers de configuration de la solution sur votre machine locale.

3. Configurer les paramètres : Assurez-vous de configurer les paramètres appropriés dans les fichiers de configuration, tels que les informations d'authentification pour l'API News API, les paramètres de connexion pour MongoDB, etc.

4. Construction des conteneurs Docker : Utilisez Docker-compose pour construire les conteneurs Docker à partir des fichiers de configuration. Exécutez la commande `docker-compose build` dans le répertoire racine du projet.

5. Démarrer les services : Une fois que les conteneurs Docker sont construits, démarrez les services en exécutant la commande `docker-compose up`. Cela lancera tous les services définis dans le fichier Docker-compose.

6. Vérifier l'état des services : Vérifiez que tous les services ont démarré correctement en consultant les journaux de Docker-compose ou en accédant aux interfaces utilisateur/web des services, le cas échéant.

7. Tester la solution : Une fois les services démarrés, testez la solution en utilisant des requêtes vers l'API News API, en vérifiant que les données sont correctement agrégées et publiées sur Kafka, et en consultant la base de données MongoDB pour vérifier que les données sont correctement stockées.

En suivant ces étapes, vous devriez être en mesure d'installer et de tester la solution localement sur votre machine. Assurez-vous de consulter la documentation et les guides spécifiques à chaque composant de la solution pour obtenir des instructions détaillées sur la configuration et l'utilisation.
