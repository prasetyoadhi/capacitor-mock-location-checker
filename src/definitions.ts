export interface MockLocationCheckerPlugin {
  /**
   * 
   * @param options {whiteList: Array<string>}
   */
  checkMock(options: { whiteList: Array<string> }): Promise<CheckMockResult>;

  isLocationFromMockProvider(): Promise<Boolean>;

  /**
   * 
   * @param options {packageName: string}
   */
  goToMockLocationAppDetail(options: { packageName: string }): Promise<void>;

  checkMockGeoLocation(): Promise<CheckMockResult>;
  
  //////////////////////////////////////////////////////////
  // check Root and Emulator
  //////////////////////////////////////////////////////////
  isRooted(): Promise<{ isRooted: boolean }>;
  isRootedWithBusyBox(): Promise<{ isRooted: boolean }>;
  isRootedWithEmulator(): Promise<{ isRooted: boolean }>;
  isRootedWithBusyBoxWithEmulator(): Promise<{ isRooted: boolean }>;

  
  /**
   * 
   * @param action String
   */
  whatIsRooted(action: string): Promise<{ isRooted: boolean }>;

  /**
   * Retrieve device information.
   * @returns Promise containing detailed device information.
   */
  getDeviceInfo(): Promise<DeviceInfo>;

}

export interface CheckMockResult {
  isRoot: boolean;
  isMock: boolean;
  messages?: string;
  indicated?: Array<string>;
}

export interface DeviceInfo {
  DEVICE: string;
  MODEL: string;
  MANUFACTURER: string;
  BRAND: string;
  BOARD: string;
  HARDWARE: string;
  PRODUCT: string;
  FINGERPRINT: string;
  HOST: string;
  USER: string;
  OSNAME: string;
  OSVERSION: string;
  V_INCREMENTAL: string;
  V_RELEASE: string;
  V_SDK_INT: string;
}