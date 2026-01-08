# SwingDoctor - Healthcare Management System (Java Swing MVC)

## Overview
This project implements a simplified UK healthcare management system using **pure Java** (no database, no JavaFX, no external libraries).
Data is loaded from CSV files and managed in-memory. The GUI is built with **Java Swing** using an MVC-style structure.

## Features
- Load and save CSV data: patients, clinicians, facilities, staff, appointments, prescriptions, referrals
- Role-based login (Patient / Clinician / Admin) with data visibility filtering
- Referral management uses a **Singleton** coordinator
- Export prescriptions/referrals to text files (no real email sending)

## Run (PowerShell)
From the project root:

```powershell
$srcFiles = Get-ChildItem -Recurse -Filter *.java -Path src | Select-Object -ExpandProperty FullName
javac -encoding UTF-8 -d out $srcFiles
java -cp out app.Main
```

## Data Files
The app expects these files in the project root:
- `patients.csv`
- `clinicians.csv`
- `facilities.csv`
- `staff.csv`
- `appointments.csv`
- `prescriptions.csv`
- `referrals.csv`

## Output
- Exported text files are saved to `output/`
- If CSV files are read-only, **Save All** will write updated CSVs into `output/`

