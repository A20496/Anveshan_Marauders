@echo off
powershell -Command "$signalStrengthOutput = netsh wlan show interfaces | Select-String -Pattern 'Signal' -Context 0,4; $signalStrengthOutput | ForEach-Object { ($_ -split ':')[1].Trim() }"