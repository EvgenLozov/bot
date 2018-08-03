Зайти в папку проекта

 1) Создать приложение на Хероку: 
	heroku create

 2) Вы можете использовать след. команду чтобы подтвердить, что для вашего приложения установлено удаленное имя heroku:	
	git remote -v

	должно вывсети что-то типа 

	heroku  https://git.heroku.com/thawing-inlet-61413.git (fetch)
	heroku  https://git.heroku.com/thawing-inlet-61413.git (push)

 3) Добавить код проекта в репо хероку 
	git push heroku master
	
	после этого должно вывести логи процеса сборки проекта
	
	Counting objects: 4, done.
	Delta compression using up to 4 threads.
	Compressing objects: 100% (4/4), done.

		....бла бла

	remote: Verifying deploy... done.
	To https://git.heroku.com/pacific-brook-38852.git
   	0120d75..eed2655  master -> master

 4) Выполнить команду запуска приложения на хероку
    В вашем случае :
	heroku run java -jar savkin/target/savkin-1.0-SNAPSHOT-jar-with-dependencies.jar

 5) Готово..



