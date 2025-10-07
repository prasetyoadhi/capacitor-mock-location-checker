import { registerPlugin } from '@capacitor/core';
const MockLocationChecker = registerPlugin('MockLocationChecker', {
    web: () => import('./web').then(m => new m.MockLocationCheckerWeb()),
});
export * from './definitions';
export { MockLocationChecker };
//# sourceMappingURL=index.js.map