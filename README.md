# UGTask

Aplikacja konsolowa w Javie 21 przelicza ceny komputerów z USD na PLN,
pobierając średnie kursy z REST API NBP w formacie JSON. Wyniki zapisuje do SQLite
oraz XML. Umożliwia wyszukiwanie komputerów po fragmencie nazwy lub dacie księgowania
oraz sortowanie wyszukanej listy komputerów rosnąco po nazwie lub dacie zaksięgowania.

## Uruchomienie

Wymagane są JDK 21, IntelliJ IDEA oraz internet do pobrania zależności i kursów NBP.

1. Otwórz folder z `pom.xml` w IntelliJ jako projekt Maven.
2. Ustaw Project SDK na JDK 21 i załaduj zależności Maven.
3. Ustaw katalog roboczy konfiguracji uruchomienia na folder z `pom.xml`
   (zwykle `$PROJECT_DIR$`).
4. Uruchom metodę `main` w klasie `pl.kosieradzki.ugtask.Main`.

Alternatywnie, w CMD lub PowerShellu przejdź do folderu z `pom.xml` i wykonaj
(wymagane JDK 21 i Maven dostępne w `PATH`):

```shell
mvn compile exec:java
```

`compile` kompiluje projekt, a `exec:java` uruchamia klasę `Main` wskazaną w `pom.xml`.

Baza `data/ugtask.db` tworzy się automatycznie i przy pustej tabeli zostaje
uzupełniona danymi z zadania. Kolejne uruchomienia korzystają z zapisanych danych.
Plik `data/Komputery.xml` jest generowany przy każdym uruchomieniu.
Nie trzeba instalować serwera bazy ani podawać haseł lub kluczy API.

## Wyświetlanie listy komputerów

Wpisz fragment nazwy albo datę w formacie `dd.mm.rrrr`, a po dwukropku
kolumnę sortowania: `nazwa` lub `data`. Przykłady:

```text
ace:nazwa
12.07.2026:data
```

Pusty Enter kończy program.

Jeśli dla daty zakupu nie ma notowania NBP (np. 12.07.2026 wypada w niedzielę),
program szuka ostatniego wcześniejszego kursu, maksymalnie 7 dni wstecz.
Data księgowania pozostaje datą zakupu podaną w zadaniu.

## Wnioski po ocenie projektu

W informacji zwrotnej wskazano potrzebę dopracowania obsługi użytkownika:
brak komendy ponownie wyświetlającej wszystkie rekordy, komunikatu o braku
wyników oraz wyboru kierunku sortowania. Zwrócono również uwagę na brak testów
jednostkowych i potrzebę lepszego opisania kodu komentarzami.

Interfejs konsolowy był jedną z form dopuszczonych w zadaniu. Wybór sortowania
wyłącznie rosnącego wynikał z interpretacji wymagań, które nie określały kierunku.
Ocena projektu pokazała jednak, że wygoda obsługi wymaga uwagi także w niewielkiej
aplikacji konsolowej.

Planowane usprawnienia:

- Czytelne menu z możliwością powrotu do pełnej listy komputerów.
- Jednoznaczny komunikat o braku wyników wyszukiwania.
- Wybór sortowania rosnącego lub malejącego.
- Testy przeliczania kwot, zaokrąglania i obsługi dat.
- Komentarze wyjaśniające istotne założenia i decyzje.

Powyższe punkty stanowią plan rozwoju; nie są jeszcze zaimplementowane
w aktualnej wersji aplikacji.
