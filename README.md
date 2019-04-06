Crypto tax calculator
=====================

A basic cryptocurrency tax calculator that uses the Delta app's exported CSV to 
calculate the cost basis that can be entered in IRS's Schedule D (Form 1040)
Capital Gains and Losses.

### Disclaimer

This application was written because I was too lazy to use excel to do the sorting
and grouping of the transactions that I have added in the Delta app to calculate my
cost basis. It was a quick and dirty solution so, might contain bugs and lacks best
practices.

At this point, this is intended to be used only with the exported CSV from the Delta
app. The code assumes the CSV output format from Delta as of March 2019 - which can be
found in this repository `src/main/resources/delta_import_template_march_2019.xlsx`.

### Running the application

#### IDE

In the IDE of your choice, with Lombok plugin enabled, run the main class
`com.prayasb.tools.cryptotaxcalculator.CryptoTaxCalculatorApplication` with 
the path to the CSV exported from the Delta app as the first argument (double-quotes
required if your path contains spaces).

#### Executable JAR

- Run `mvn clean install` on the project root
- From terminal, change the directory to the `target` directory
- Execute the `.jar` file with the path to CSV exported from the Delta
app as the first argument (surround the path with double-quotes if the path contains
spaces)
