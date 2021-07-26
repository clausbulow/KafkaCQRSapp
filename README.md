# CQRS PoC
Denne applikation er en PoC. 
Dens formål er at illustrere hvorledes CQRS og eventsourcing kan implementeres.

Applicationen står ikke alene. Den eksistere i en kontekt hvor eksterne databaser, Kafka og Kafka connectors også er tilstede i systemlandskabet.

Konteksten kan illustreres således:

![Diagram Context](http://www.plantuml.com/plantuml/png/dPBDhjem48NtVWgBJLSfKfSHGXJeecgHsbAfwy4Pg8L_Wd-ewDFNZi4nxbtatbryvZiJU-Qih0FZl1JatM_dUjlEvxq05PNtJCkvnOj7nR3fE9mCI4B0EwsyFA0Xv5lmSiRxOA0NVqDpes3fxBkFNplTePZHnOAklUKAhVtOzu8pS5mhkbm-kRboQ1I8mV_vSTW54vouEarjyyTmX_77gL59_egbvbMN4fg1sAu7vz3lQnAc-gq3XtkdJNBluv4EvzaJnxHIo88-O5iyu5_k9Tqyv6VwOFGP4_e5ZcUWwoXKa3EWx54RUITPnpvykg9oFyT2V4Rgi7S6GLeAurkgfnXa7Aze_Xkh75Q46PWw6uFGlRoe7w7ofZr5uzGraNjScsMENXi6BYMeZZoKMCs9AkgZLXDX0Fnugu0oHYtrVGSEopXM-9YQDjz-M1qfyjG4CCSct6oxIKZMGgUKbBAQX6Wk0zBc-YWcAF7ZDUekUUiralG4vG7NK3dc78SQoeL0bOlEWnpZGbQeEY_5Fm00)
De aktører, der her indgår i diagrammet med stereo-typen "BusinessApplication" indgår i denne PoC.

## PoC'ens tilstand
Poc er i kørende tilstand i stand til:
1. At generere commands ud fra et request
2. Lægge commands på en kommando-kø i applikationens interne køsystem (ikke kafka)
3. Aftage disse commands i en write model, validere indholdet af commands, ajourfører sin write model og kaste en tilhørende forretningshændelse (via DB -> Connector -> Kafka->Streaming Application -> Kafka)
4. Gribe BusinessEvents fra Kafka og overbringe dem til det applikationens interne køsystem.
5. Ajourføre read model på baggrund af det modtagne Business-Event

Derudover undstøtte PoC'en:
1. Skabelsen af Snapshot
2. Initialisering af write- og readmodel på baggrund af Business Events og snapshots


PoC'en mangler:
1. Fejlhåndtering
2. Håndtering "overskrivelse af data". Det skal nemlig sikres, at en bruger ikke overskriver en nyere version af data, en den der danner grundlag for opdateringen
3. Unit-tests
4. Generaliseringer - mange ting kan udskilles til en generelt modul
5. Refaktureringer

## Persisteringer
Der indgår 3 typer af persistering i PoC'en
1. Persistering af BusinessEvents (og tilhørende)
2. Persistering af forretningsdata i Write-modellen
3. Persistering af forretningsdata i Read-Modellen

Figuren hér illustrerer dette
![persistence](http://www.plantuml.com/plantuml/png/ROzDIyD048Rl-HL3BkeXBUJOW-0N1A5GHF0W7gR9D5tDpgoxau18_xkJrfRMUbZOtNdUTv-zMAk6D9jEGuUilUycjVYfhI1Ch4NZi8teiomBM7zWIv3VXD2v6jK9v_2T0Ro-HAS4SsceUuEYk8Q596qZ9PZ1RPySKqhmC5XV-XT_nm6FqNjK7C4dmkQOgyePB2YcGBMwWK3uJsPZderjbfXCT6ynMcwE1rLvTlLe2iKS0rmwBZnvYLzNEM3QkMrX5hDUYgfuaDLsO9BFQzenOpZV5-RWBDed1OJaVEW87CEk5onLOLfx2zJ2YeuTjw1MTF8RYFJPkqWDgC20dRDoikoTU8_Sf4b6tC1eTUPMbRzcLGc7FveHygc67KAKUdnnZnrAVm00)

Bemærk, at den eneste type er persisteringer, der er bestandige i persistering af businessEvents i eventstoret i Postgres.
Forretningsdata persisteres i stores, der in-memory og som forgår, når applikationen genstartes.
Dette er dog ikke det store problem, da forretningsdate genetableres på baggrund af businessEvents, når servicen genstartes.

## Snapshotting
Ud over, at persisteret BusinessEvent i Postgress-databasen, kan der også persisteres "Snapshots".
Et snapshot er et øjebliksbillede af en instanse af et aggregat/en entititet. Et snapshot af samtlige 
instanser i et aggregat kan foretages på anfording - og bør på sigt foretages periodisk.

Snapshotting foretages for at optimerer indlæsningen af forretnings-events.
Når et Snapshot for en given instans forforefindes, kan dette indlæses, og man kan da nøjes med at indlæse 
businessEvents fra tidspunktet for hvornår dette snapshot er foretaget og frem i stedet for at læses alle forretningsevents 
tilbage i tid.

Tabeller til understøttelse for for forretnings-events og snapshots ser således ud:

![Entities](http://www.plantuml.com/plantuml/png/fO-n2i8m48RtF4Ks9-rk9w4EdKjdEPCZ3RP9p5qBHNntGrk7egBWBU7_o1tVlscO0-spBBJWkE_QdV9TfbeWsceB9a8dn1HbvI1GuvdaJSWqTLqMqkfy2XL7wupaCU0Ad7ko3eakEAuuAFPn9grFUS18rhkP5C0eLSJqw1FmQb43GoxkYzF1c8Wc_QCtJg-7lqZDzi_Rnm4TKzB4ptr_gBUGYDSUYKlzJsdn0000)

"Versions"-attributen skal bemærkes i disse entiter.
Hver gang, at event lægges i Eventstoret eller et snapshot lægges i "Snapshots" lægges ændringversionen for den givne instans af det aggregat - som er repræsenteret ved "businessKey", med i versionsfeltet for henholdvis eventet og snapshottet.
Det er "Aggregates"-entitens rolle, at holde styr på den gældende version. Versionen tælles en op ved hver ændring af at aggregat og værdien gemmes i Aggregates-entititen.

Herved gøres det opnår vi en komplet tilstand for en instans af et aggregat, når vi indlæser sidst taget snapshot og alle efterfølgende events for den givne instans.
