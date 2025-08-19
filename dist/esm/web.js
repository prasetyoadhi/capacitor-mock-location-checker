import { WebPlugin } from '@capacitor/core';
export class MockLocationCheckerWeb extends WebPlugin {
    goToMockLocationAppDetail(options) {
        throw new Error('Method not implemented. options: ' + options);
    }
    isLocationFromMockProvider() {
        throw new Error('Method not implemented.');
    }
    async checkMock(options) {
        throw new Error('Method not implemented. options: ' + options);
    }
}
//# sourceMappingURL=web.js.map