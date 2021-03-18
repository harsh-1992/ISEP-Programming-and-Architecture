cd bin/

start cmd /k java echo.with.fault.tolerance.ServerMain localhost:1099
timeout 1
start cmd /k java echo.with.fault.tolerance.ServerMain localhost:1250 localhost:1099
timeout 1
start cmd /k java echo.with.fault.tolerance.ServerMain localhost:12345 localhost:1099

timeout 1
start cmd /k java echo.with.fault.tolerance.ClientMain localhost:1099