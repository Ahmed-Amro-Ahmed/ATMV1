# ATM System V1 - Testing Plan

## Unit Testing
- Test each method in AccountV1 (deposit, withdraw, transfer, authenticate)
- Test CashBinV1 and PrinterV1 methods
- Test PersistenceManager save/load functions

## Integration Testing
- Test customer authentication and transaction flow
- Test technician maintenance/repair flow
- Test data persistence after transactions

## System/Acceptance Testing
- Simulate full ATM session (login, transaction, logout)
- Test error handling (invalid PIN, insufficient funds, etc.)

## Testing Strategies
- Use both white-box (code-level) and black-box (user-level) testing
- Validate with sample data in JSON/TXT files
