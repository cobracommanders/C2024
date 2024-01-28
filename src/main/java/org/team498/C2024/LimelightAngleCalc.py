import numpy as np
import matplotlib.pyplot as plt

# Constants
r = 0.25  # Length of the lever in meters

# Generating x values from -0.25 to 0.25
x_values = np.linspace(-0.25, 0.25, 500)

# Calculating theta values in radians
theta_values = np.arccos(x_values / r)

# Converting theta from radians to degrees for better understanding
theta_values_degrees = np.degrees(theta_values)

# Plotting
plt.figure(figsize=(10, 6))
plt.plot(x_values, theta_values_degrees, label='Theta (degrees)')
plt.title('Required Camera Angle (Theta) vs Horizontal Position (x)')
plt.xlabel('Horizontal Position (x) in meters')
plt.ylabel('Camera Angle (Theta) in degrees')
plt.grid(True)
plt.legend()
plt.show()