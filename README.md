# Calculadora PJ

App Android nativo que calcula quanto sobra no fim do mês para quem trabalha como PJ:
informe as horas e o valor/hora, escolha o regime tributário e veja o líquido a receber,
com o detalhamento dos descontos. Cada mês pode ser salvo e comparado no histórico.

<img width="2314" height="1516" alt="image" src="https://github.com/user-attachments/assets/99473748-10c2-455e-9bc4-ac623c159361" />

## Regimes tributários

| Regime | Como incide | Configuração |
| --- | --- | --- |
| Alíquotas | INSS, ISS e IRRF em percentual sobre o bruto | padrão 11% / 5% / 1,5% |
| MEI | DAS, valor fixo mensal que não acompanha as horas | você informa o valor da guia |

O regime usado fica gravado em cada mês salvo. Trocar de regime depois não reescreve o
histórico: um mês salvo como MEI continua mostrando o DAS.

## Stack

- Kotlin 2.2.10, Compose com Material 3, tema escuro fixo
- MVVM em três camadas: `ui`, `domain`, `data`
- Hilt para injeção de dependência, KSP para geração de código
- Room para o histórico, DataStore Preferences para os padrões
- Coroutines e `StateFlow`

## Requisitos

- JDK 21
- Android SDK com a plataforma **android-37** instalada
- Um emulador ou aparelho com Android 8.0 (API 26) ou superior

O caminho do SDK vem de `local.properties`, que não é versionado. Se o arquivo não
existir, crie com:

```properties
sdk.dir=/caminho/para/Android/sdk
```

## Build

```bash
./gradlew :app:assembleDebug
```

O APK sai em `app/build/outputs/apk/debug/app-debug.apk`.

Para instalar em um aparelho conectado:

```bash
./gradlew :app:installDebug
```

## Testes

Unitários, rodam na JVM sem aparelho:

```bash
./gradlew :app:testDebugUnitTest
```

Instrumentados, precisam de emulador ou aparelho conectado. Cobrem o SQLite real e o
DataStore real, que é onde erro de mapeamento aparece:

```bash
./gradlew :app:connectedDebugAndroidTest
```

Relatórios em `app/build/reports/tests/` e `app/build/reports/androidTests/`.

## Estrutura

```
com.pjcalc/
├── ui/
│   ├── home/        Home, Resultado e seus ViewModels
│   ├── history/     Histórico, detalhe do mês e gráfico
│   ├── settings/    Ajustes dos padrões e do regime
│   ├── splash/      Logo textual de abertura
│   ├── components/  Design system: campos, botões, cards, breakdown
│   └── theme/       Cores, tipografia e tema escuro
├── domain/
│   ├── model/       RegimeTributario, RegistroMes, ResultadoCalculo, Desconto
│   ├── usecase/     CalcularGanhoUseCase
│   └── ...          Parsing e formatação em pt-BR
├── data/
│   ├── local/       Entidade, DAO e banco Room
│   ├── prefs/       DataStore dos padrões
│   └── repository/  Repositório do histórico
└── di/              Módulos Hilt
```

## Decisões que valem saber

**`android.disallowKotlinSourceSets=false` em `gradle.properties` é obrigatório.**
O AGP 9 usa Kotlin embutido e o KSP registra os fontes gerados pelo DSL antigo. Sem
essa linha o build falha na configuração, antes de compilar qualquer coisa.

**O banco usa migração destrutiva.** Atualizar a versão do schema apaga o histórico
local. Antes de publicar para usuários reais, troque por uma `Migration` de verdade.

**O DAS não tem valor padrão.** Ele depende do salário mínimo vigente e do tipo de
atividade, então o campo começa vazio em vez de trazer um número que envelhece errado.

**Valores monetários usam `Double`.** É o suficiente para estimativa, mas não para
cálculo fiscal com garantia de centavo. `BigDecimal` seria o certo se o app virar
fonte de verdade contábil.

**O gráfico do histórico tem base no zero.** Meses com ganhos parecidos produzem barras
parecidas, de propósito: truncar o eixo deixaria o gráfico mais bonito e mentiroso.
