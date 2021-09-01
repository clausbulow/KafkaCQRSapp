# CQRS PoC

Denne applikation er en PoC. Dens formål er at illustrere hvorledes CQRS og eventsourcing kan implementeres.

Applikationen er inspireret af følgende artikler:

1. [Playing with cqrs and eventsourcing...](https://blog.nebrass.fr/playing-with-cqrs-and-event-sourcing-in-spring-boot-and-axon/)
2. [Building an Event Store](https://cqrs.wordpress.com/documents/building-event-storage/)

Applicationen står ikke alene. Den eksistere i en kontekt hvor eksterne databaser, Kafka og Kafka connectors også er
tilstede i systemlandskabet.

Konteksten kan illustreres således:

![Diagram Context](http://www.plantuml.com/plantuml/png/bPDDRzmW48Rl_0gqbwO7PwKUhMYbx6waLcsaj5krPnQF7RHyE1XIkR--W5aCRbKvDpEyymupy_ZTip7zK6-tFIVIMC5lgHBR0LuiI0fLmqYdYK28MAEa5MVG23qHRHXblKl0BtO4FM8ouCrNwxyULGDyW-_kyDuEJC8mtFSzPvGOfYJUxOfi9WreINZ8_tGzFBeaqZ7PhRNvfPc1K3WOtNUTXeuOM9lFMGk7aNBuI2HlG0UNBrFm4g8D1_q_jz9EKSAF-v_-LPFL55ZxdC6ujv-CqbFsoHznE6yA7LLI0dNn83l26NupA_3X6YxLPwqkiRNFfBqGl0-1KcGqaKEhj8XAsj0F3wyWpVVvmhSnEPoC1Y86JEPU5gree0nUerdUiWWxXYHOEnjNiCasdGc_kPlgJVJ5FVd4jMj2aVTgbzYjtS25S8YWHBExdP5Czn4ZbD6w4Fu0pTen-PN29QGrzcyRqql3cj1VA2zAUDL9MKtTxnKVX6yAY_VBkXdDDRPzusaCfsdQ_omv8rpZbca8piiw6UULoqaukuaT3rCxC-9EC16StKQaVN86RftkSvghpA0SMUPofN4fIHXNnU9BKRx-XEaqSHD7lPp0JFCyQ8zZclCCDKBe3m00)
De aktører, der her indgår i diagrammet med stereo-typen "BusinessApplication" indgår i denne PoC.

## PoC'ens tilstand

Poc er i kørende tilstand i stand til:

1. At generere commands ud fra et request
2. Lægge commands på en kommando-kø i applikationens interne køsystem (ikke kafka)
3. Aftage disse commands i en write model, validere indholdet af commands, ajourfører sin write model og kaste en
   tilhørende forretningshændelse (via DB -> Connector -> Kafka->Streaming dk.ksf.application.Application -> Kafka)
4. Gribe BusinessEvents fra Kafka og overbringe dem til det applikationens interne køsystem.
5. Ajourføre read model på baggrund af det modtagne Business-Event

Derudover undstøtte PoC'en:

1. Skabelsen af Snapshot
2. Initialisering af write- og dk.ksf.application.readmodel på baggrund af Business Events og snapshots

PoC'en mangler:

1. Fejlhåndtering
2. Håndtering "overskrivelse af data". Det skal nemlig sikres, at en bruger ikke overskriver en nyere version af data,
   en den der danner grundlag for opdateringen
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

Bemærk, at den eneste type er persisteringer, der er bestandige i persistering af businessEvents i eventstoret i
Postgres. Forretningsdata persisteres i stores, der in-memory og som forgår, når applikationen genstartes. Dette er dog
ikke det store problem, da forretningsdate genetableres på baggrund af businessEvents, når servicen genstartes.

## Handlers

Løsningen arbejder arbejder med 3 typer af handlers, nemlig:

- CommandHandlers
- Event Sourcing Handlers
- Event Handlers

### Så - hvor og hvornår anvendes hvilke handlere?

1. Commands skabes via en commandcontroller (som f.eks. aktiveres fra en rest-service) og aftages i en Command-handler i et "Aggregate". Aggregaten skal validerer
   kommandoen og dernæste skabe et tilhørende forretningsevent.
2. Forretningsevent/Business events skabes i et Aggregate og aftages dels i aggregatet selv i en "event sourcing
   handler" og dels af en eller flere "Event Handlers", der hvert hører sit "Perspektiv" til.
   "Event Sourcing handlers" opgave er at opdatere den instans af aggregatet, som Business Eventen addresserer.
   "Event handlers" opgave er, at opdatere et pespektiv på de data, som et business-event bærer på.

I et simplificeret kontext diagram, ser sekvensen ved opdatering via en command således ud:
![Handlers](http://www.plantuml.com/plantuml/svg/bPNBZfim58RtVWehcyaiQFPeX9IabMRKLeecLRjsp0bZ1MpgIwHv-ng8YGs6q1qs_Z-Vkvy-jAvLkjdK9U7QLEKNAggDWhy6E8KaPwIGf4A862sugKuW4JeGgHbbjHLWAhYMeYn1OgBmQY-gYl0Sx_lj5NvvmJkZ60UbjdLTCaeq4nndMG22At3zQhKEzStRQ24Ds-hs8eUXc1I5X89eUEDA4sjuIzYwtQKsz1W9jL1C2_aPWjxx_GOP8Gw0fQ2a_Bhx8vdkJFhHROpyYKRZQ2_ohhn7HJK_S6YCHnkmR4GqjAoMSEoNyx8BELz8g_ZUV3M7t_XP4gMbeTh8w7cRTsMgMmPR7NPRyxVL85KDLBDhP-NXlbwQFGyn2DeXzcUrZ7fBuJkGV9J12Jb2NbSaML3QARwro6szfjtoEUrfROfHq0yZo9dnF0ID195YxdJROREWMDrRWb-zwRZ7K53TKpUIfRU53evba_wv4obc_2ekC15WXCPJ8exv3PATFrsYuhBGMWcs8JW--HbKDZwYncSXCH3wSJi3UNWAOVmuqnrF64aPBro-nyB9YBJwOwXV1sOyMzpIy9Wwj-k-YjfvCgRDUtM2Wl5Vad15QDCsSzxurQJ85TOZcmBhvsfuMXurTp20EttGfXD-3oUt1Twdy7BCPFY0vs7ml1cV95axYbESCpkhsSdeHvTrezu9tS1kofeoU_6SoXrEij12nGfEolzmSWZewzxLwsvUxKt_eEmdvPx0nZVwUBXQRuR_J_jcbMCNeheayrwDXn2RJFja_0C0)

### Implementering af handlere

Handlere skabes ved at hjælp af annoteringer i koden - således kan en command-handler skabes således:

```
    @CommandHandler(createsAggregate = true)
    public KlientOprettetObject opretKlient(MessageContext context, OpretKlientCommand command) throws Exception {
        return KlientOprettetObject.builder().
                cpr(command.getCpr()).
                efternavn(command.getEfternavn()).
                fornavn(command.getFornavn()).
                build();
    }
```

En event sourcing handler skabes tilsvarende:

```
    @EventSourcingHandler
    public void onKlientOprettetEvent(MessageContext context, KlientOprettetObject event) throws Exception {
        fornavn = event.getFornavn();
        efternavn = event.getEfternavn();
        cpr = event.getCpr();
        version = context.getVersion();
        log.info("Klient oprettet i dk.ksf.application.writemodel");
    }

```

Det skal bemærkes, at et Event Sourcing handler ALTID modtager objekter af "BusinessEvent-typen". Det der adskiller den
ene eventsourcing handler fra den anden er det forretningsobject, der er aggregeret i strukturen. I det viste tilfælde
er dette object af typen "KlientRettetObject".

En eventhandler implementeres i analogi med en eventsourcing-handler således:

```
    @EventHandler
    @Transactional
    public void onKlientRettetEvent(MessageContext context, KlientRettetObject event) throws Exception {
        retKlient(event, context.getVersion());
        log.info("Klient rettet i read-model");
    }
```

### Event Sourcing- og Events Handlers - sourcing ved initialisering

Når applikationen startes vil en initialiseringssekvens sikre, at event- og eventsourcing handlers kaldes, således at
såvel "Aggregates" og "Perspectives" kan initialesere sig selv med sine egne data. Husk at: Event Sourcing handler
tilhører en eller flere typer aggregater i skrivemodellen og at Event Handlers tilhører et eller flere perspektiver i
læsemodellen.

Initialiserings-sekvensen kan illustreres således:

```
For hvert type af aggregate (klient, retskreds)
  For hvert instans af aggregate (klient 050878-0517)
    Find første event og eftefølgende events hørende til eventstore eller snapshot
    For hver event:
      Signaler "Event Sourcing Event"  (til "Event Sourcing Handlers" i aggregate / dk.ksf.application.writemodel i applikationen)
      Signaler "Event" (til "Event Handlers" i Perspectives / dk.ksf.application.readmodel)
       
```

Bemærk, at events kun distribueres inden for applikationen. Ingen events udløst af initialiseringseksvensen publiceres
ud til eksterne lyttere!.

### Event Sourcing handlers og Aggregates

Alle Event Sourcing handlers skal være indholdt i en klasse, der annoteres med @Aggregate - et eksempel ses her.

```
@Aggregate(aggregateType = AggregateTypes.klient, repository = KlientWriteModelRepository.class)
public class KlientAggregate  {
    @AggregateIdentifier
    String cpr;
    String fornavn;
    String efternavn;
    long version;

```

I dette tilfælde, vil aggregatet understøtte instanser af typen "klient" og anvende repositoriet "
KlientWriteModelRepository" til at hente og persisterer instanserne, når "commands" behandles.

@AggregateIdentifier anvendes af den bagvedliggende besked motor til at fremfinde og gemme den rette instanse af
aggregatet og kalde command handleren til netop denne instans. For dette opslag skal lade sig gøre, så er der brug for
endnu en information - nemlig informationen om, hvilken en instanse af aggregate en besked vedrører. Denne informaton
bringes til veje, ved hjælp af en annotering i command klassen

```
public class OpretKlientCommand extends Command {
    @TargetAggregateIdentifier
    String cpr;
    String fornavn;
    String efternavn;
}
```

Her beskriver '@TargetAggregateIdentifier' hvilken attribut i kommandoen, som bærer nøgleværdien for aggregatet, der
skal opdateres.

## Registering af Typer af Business Events ved initialisering

Det er nødvendigt med noget bagvedliggende bogholderi i applikationen, for at holder styr på, hvilken eventhanlder, der
skal kaldes, når et business-events skal eksekveres. Kom i hu, at et business-event er fast defineret således:

```
public  class BusinessEvent<T>  implements ResolvableTypeProvider {
private String eventNavn;
private String requestId;
private String key;
private String actor;
private long version;
private  Instant created_at;
private AggregateTypes aggregateType;
T object;

    @Override
    @JsonIgnore
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), object.getClass());
    }
}
```

Metode "getResovlableType" er en teknikalitet, der er alene er tilstede, for at det kan identficeres på
runtime-tidspunktet, hvilken business-object, der knyttet til forretnings-eventet. Et business-object kan være defineret
således:

```
@BusinessObject(eventName = "klientOprettet_event")
public class KlientOprettetObject {
    String cpr;
    String fornavn;
    String efternavn;
}
```

Bemærk her annoteringen @BusinessObject(eventName = "klientOprettet_event") - det er denne annotering, der gør det
muligt for beskedmotoren, at skabe den rette informationsbærende klasse og forbinde den til et forretningsobject, når en
besked modtages.

Logikken bag dette er todelt - der sker noget ved initialisering af applikationen og noget, når en besked modtages. Ved
initialisering sker følgende:

```
Scan applikationen efter klasser, der har annoteringen "@BusnessObject"
For hvert @BusinessObject
  gem eventnavnet og den implmenterende klasse i en map(liste), som kan slås op under eventnavn
  
```

Under afvikling, sker der følgende, når et event modtages og skal processeres:

```
Lav en instanse af BusinessEvent og tildel de faste værdier
Slå eventNavn op i liste, som er lavet under initialisering
Hvis ikke eventNavn eksisterer
  så skip 
ellers
  lav en instanse af den implementerende klasse, som returneres fra liste
  tildel værdier fra event modtage til den implementernede klasse.
  Læg de nu dannede businessEvent på din internebesked-motor  
```

Herefter vil beskedmotoren kalde alle event-handlers, der understøtter businessEvents med netop denne type
business-objects vedhæftet.

## Snapshotting

Ud over, at persisteret BusinessEvent i Postgress-databasen, kan der også persisteres "Snapshots". Et snapshot er et
øjebliksbillede af en instans af et aggregat/en entititet. Et snapshot af samtlige instanser i et aggregat kan foretages
på anfording - og bør på sigt foretages periodisk.

Snapshotting foretages for at optimerer indlæsningen af forretnings-events. Når et Snapshot for en given instans
forforefindes, kan dette indlæses, og man kan da nøjes med at indlæse businessEvents fra tidspunktet for hvornår dette
snapshot er foretaget og frem i stedet for at læses alle forretningsevents tilbage i tid.

Tabeller til understøttelse for for forretnings-events og snapshots ser således ud:

![Entities](http://www.plantuml.com/plantuml/png/fO-n2i8m48RtF4Ks9-rk9w4EdKjdEPCZ3RP9p5qBHNntGrk7egBWBU7_o1tVlscO0-spBBJWkE_QdV9TfbeWsceB9a8dn1HbvI1GuvdaJSWqTLqMqkfy2XL7wupaCU0Ad7ko3eakEAuuAFPn9grFUS18rhkP5C0eLSJqw1FmQb43GoxkYzF1c8Wc_QCtJg-7lqZDzi_Rnm4TKzB4ptr_gBUGYDSUYKlzJsdn0000)

"Versions"-attributen skal bemærkes i disse entiter. Hver gang, at event lægges i Eventstoret eller et snapshot lægges
i "Snapshots" lægges ændringversionen for den givne instans af det aggregat - som er repræsenteret ved "businessKey",
med i versionsfeltet for henholdvis eventet og snapshottet. Det er "Aggregates"-entitens rolle, at holde styr på den
gældende version. Versionen tælles en op ved hver ændring af at aggregat og værdien gemmes i Aggregates-entititen.

Herved gøres det opnår vi en komplet tilstand for en instans af et aggregat, når vi indlæser sidst taget snapshot og
alle efterfølgende events for den givne instans.

